package com.lz.ht.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lz.ht.model.Role;
import com.lz.ht.result.Result;
import com.lz.ht.model.Resources;
import com.lz.ht.model.RoleResources;
import com.lz.ht.page.PageModel;
import com.lz.ht.result.Result;
import com.lz.ht.service.ResourcesService;
import com.lz.ht.service.RoleService;
import com.lz.ht.util.JwtUtil;
import com.lz.ht.base.BaseController;
import com.lz.ht.constant.SysConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.github.abel533.sql.SqlMapper;
@Slf4j
@Controller
public class ResourcesController extends BaseController{

    @Autowired
    private ResourcesService resourcesServiceImpl;

    @Autowired
    private RoleService roleServiceImpl;

    @RequestMapping(value = "/resources/list",method = {RequestMethod.GET})
    public String resources_list()throws Exception{
        return "resources/resources_list";
    }

//    @RequestMapping(value = "/resources/list",method = {RequestMethod.POST})
//    @ResponseBody
//    public PageModel list(Resources resources, PageModel<Resources> page)throws Exception{
//        page.init();
//        List<Resources> list = resourcesServiceImpl.findPageList(page,resources);
//        long count = resourcesServiceImpl.findCount(resources);
//        page.packData(count,list);
//        return page;
//    }

    @RequestMapping(value = "/resources/list",method = {RequestMethod.POST})
    @ResponseBody
    public PageModel list(Resources resources , PageModel<Resources> pageModel)throws Exception{
        pageModel.init();//分析返回的数据：pageSize  currentage
        //调用pageHelper
        PageHelper.startPage((int)pageModel.getCurrentPageNum(),(int)pageModel.getPageSize());
        //业务数据
        PageInfo<Resources> pageInfo =
                new PageInfo<Resources>(this.resourcesServiceImpl.findList(resources));

        //组装数据返回到客户端，使用的为springboot默认的Json包 spring-boot-starter-json 依赖：
        pageModel.packData(pageInfo.getTotal(),pageInfo.getList());
        return pageModel;
    }



    @RequestMapping(value = "/resources/add",method = {RequestMethod.GET})
    public String addInit(Resources resources,Model model){
        return "resources/resources_add";
    }

    @RequestMapping(value = "/resources/add",method = {RequestMethod.POST})
    @ResponseBody
    public Result add(Resources resources){
        resourcesServiceImpl.add(resources);
        return Result.genSuccessResult();
    }
    
    @RequestMapping(value = "/resources/update",method = {RequestMethod.GET})
    public String updateInit(int resKey,Model model){
        Resources resources = resourcesServiceImpl.findByresKey((long) resKey);
        model.addAttribute("resources",resources);
        return "resources/resources_update";
    }

    @RequestMapping(value = "/resources/updatein",method = {RequestMethod.POST})
    @ResponseBody
    public Result update(Resources resources){
        resourcesServiceImpl.updateById(resources);
        return Result.genSuccessResult();
    }


    // 有问题，改为根据resKey
    @RequestMapping(value = "/resources/delete",method = {RequestMethod.POST})
    @ResponseBody
    public Result delete(int resKey){
        Resources resources = resourcesServiceImpl.findByresKey((long) resKey);
    	  
    	// 若此资源有子资源也不能删除
    	String sql = "SELECT * FROM t_resources tmp WHERE tmp.presKey = ( " + 
    			"SELECT r.resKey FROM t_resources r WHERE r.id = #{id} )" ;
    			 
        List<Resources> selectList = sqlMapper.selectList(sql, resources.getId(), Resources.class);
        if(selectList!=null && selectList.size()>0) {
        	return Result.genResult("997", "此资源有子资源，不能删除");
        }
        //查找所有拥有此资源的角色，若有角色使用此资源就提示不能删除
         String sql2 = "SELECT * FROM " +   
         		"  t_role_resources rr " + 
         		"  WHERE rr.resKey IN " + 
         		"  (SELECT " + 
         		"    t2.resKey " + 
         		"  FROM " + 
         		"    t_resources t2 " + 
         		"  WHERE t2.presKey = " + 
         		"    (SELECT " + 
         		"      r.resKey " + 
         		"    FROM " + 
         		"      t_resources r " + 
         		"    WHERE r.id = #{id}) " + 
         		"    UNION " + 
         		"    SELECT " + 
         		"      r.resKey " + 
         		"    FROM " + 
         		"      t_resources r " + 
         		"    WHERE r.id = #{id}) "  ;
 
          
          Map<String,Long> map =new HashMap<String, Long>();
          map.put("id", resources.getId());
          List<RoleResources> selectList2 = sqlMapper.selectList(sql2, map, RoleResources.class);
          if(selectList2!=null && selectList2.size()>0) {
          	return Result.genResult("996", "有角色使用此资源，不能删除");
          }
          //RABC 模型不能删除
           
          // return Result.genResult("995", "系统模块，不能删除");
         
          String sql3 = " SELECT DISTINCT tt.id FROM t_resources tt WHERE tt.resKey IN ( " + 
          		" SELECT  t2.resKey FROM t_resources  t2 WHERE t2.resKey = "+SysConstant.SYSTEM_MANAGE_RESOURCE_RESKEY+" OR t2.presKey = "+SysConstant.SYSTEM_MANAGE_RESOURCE_RESKEY+") OR tt.presKey " + 
          		" IN (SELECT  t1.resKey FROM t_resources  t1 WHERE t1.resKey =  "+SysConstant.SYSTEM_MANAGE_RESOURCE_RESKEY+ " OR t1.presKey =  "+SysConstant.SYSTEM_MANAGE_RESOURCE_RESKEY+ ")";
           
          List<Map> selectList3 = sqlMapper.selectList(sql3, Map.class);
          boolean flag = false;
          for(Map m : selectList3) {
        	  String id = m.get("id").toString();
        	  if(id.equals( resources.getId().toString()) ) {
        		  flag = true;
        		  break;
        	  }  
          }
          if(flag) {
        	  return Result.genResult("995", "系统模块，不能删除");
          }
          resourcesServiceImpl.deleteById(resources.getId());
       
          return Result.genSuccessResult();
    }



    @Autowired
    private SqlMapper sqlMapper;

    private Gson gson = new Gson();

    @RequestMapping(value = "/resources/selectPageList",method = {RequestMethod.GET})
    public String selectPageList(){
        return "resources/resources_selectPageList";
    }

    @RequestMapping(value = "/resources/selectPageList",method = {RequestMethod.POST})
    @ResponseBody
    public PageModel selectPageList(Resources resources, PageModel<Resources> page)throws Exception{
        page.init();
        List<Resources> list = resourcesServiceImpl.findPageList(page,resources);
        long count = resourcesServiceImpl.findCount(resources);
        page.packData(count,list);
        return page;
    }


    @RequestMapping(value = "/resources/choosePage",method = {RequestMethod.GET})
    public String selectPage(String fkName,Model model){
        if(StringUtils.isNotEmpty(fkName)){
            Map<String, Object> foreignKeyMap = sqlMapper.selectOne("select * from t_fkeys where fkName = \'"+ fkName+"\'" );
            String rSql = foreignKeyMap.get("rSql").toString();
            log.info("rSql:{}",rSql);
            List<Map<String, Object>> relativeMapList = sqlMapper.selectList(rSql);
            if(relativeMapList!=null){
                model.addAttribute("relativeMapList",relativeMapList);
                String rType = foreignKeyMap.get("rType").toString();
                if("0".equals(rType)) return select_radioPage(model);
                if("1".equals(rType)) return select_radioPage(model);
                if("2".equals(rType)) {
                    String coverOtherValueColumn = foreignKeyMap.get("coverOtherValueColumn").toString();
                    model.addAttribute("coverOtherValueColumn",coverOtherValueColumn);
                    model.addAttribute("treeNodes",toJson(relativeMapList));
                    return select_zTreePage(model);
                }
            }else{
                return "error/error";
            }
        }
        return "error/error";
    }
    @RequestMapping(value = "/resources/management",method = {RequestMethod.GET})
    public String roleResourceInit(Role role, Model model) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Long userId = (Long) session.getAttribute("loginUserId");

        Long roleId = resourcesServiceImpl.findRoleIdByUserId(userId);
        //String rolekey = (String) session.getAttribute("loginUserId");

        role = roleServiceImpl.findById(roleId);
        //根据role 查找roleResource表中，本角色管理的资源
        //SELECT  t.id AS id, t.deptName AS `name`, t.parentId pId , 'true' AS `open` FROM t_dept t
        String roleKey = role.getRoleKey();
        //1.查询所有的资源列表
        //2.查询角色对应的资源
        //3.遍历所有资源列表，如果角色对应的资源列表中有，就给他标识为选中状态
        String sql = "    SELECT  res.resKey AS id, " +
                "    res.name AS name, " +
                "    res.presKey AS pId, " +
                "    'true' AS open,     " +
                "    CASE WHEN   res.resKey IN ( SELECT r.resKey FROM t_role_resources r WHERE r.roleKey = "+
                "  #{roleKey} )  THEN 'true' ELSE 'false' END AS checked  " +
                " FROM  t_resources res ";

        List<Map> selectList = sqlMapper.selectList(sql,  roleKey, Map.class);

        model.addAttribute("treeNodes",toJson(selectList));
        model.addAttribute("roleKey",roleKey);
        return "resources/resources_management";
    }

    @RequestMapping(value = "/resources/managelist",method = {RequestMethod.GET})
    @ResponseBody
    public Map<String ,Object> manageList(int resKey){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Resources resources = resourcesServiceImpl.findByresKey((long) resKey);
        System.out.println(resources);
        //model.addAttribute("resources",resources);
        modelMap.put("data", resources);
        return modelMap;
    }


    private String select_radioPage(Model model){
        model.addAttribute("demo","demoValue");
        return "resources/resources_selectPageList";
    }

    private String select_zTreePage(Model model){
        model.addAttribute("demo","demoValue");
        return "resources/resources_foreignKeyTree";
    }
    public String toJson(Object obj){
        String s = gson.toJson(obj);
        return s;
    }


}
