package com.lz.ht.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lz.ht.dao.CostCheckMapper;
import com.lz.ht.dao.CostProjectMapper;
import com.lz.ht.model.CostCheck;
import com.lz.ht.model.CostProject;
import com.lz.ht.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CheckController {
    @Autowired
    private CostCheckMapper costCheckMapper;

    @RequestMapping(value = "/check/list",method = {RequestMethod.GET})
    public String project_list()throws Exception{
        return "check/check_list";
    }

    @RequestMapping(value = "/check/list",method = {RequestMethod.POST})
    @ResponseBody
    public List<CostCheck> queryAllUser() {
        return costCheckMapper.findAll();
    }

    @RequestMapping(value = "/check/queryAllUserByPage",method = {RequestMethod.POST})
    @ResponseBody
    public PageInfo<CostCheck> queryAllUserByPage(CostCheck costCheck, int currentPageNum, int pageSize)throws Exception{
        //[pageNum, pageSize]  页码  每页显示数量
        PageHelper.startPage(currentPageNum,pageSize);
        PageInfo<CostCheck> pageInfo = new PageInfo<>(costCheckMapper.findAll());
        return pageInfo;
    }

//    @RequestMapping(value = "/check/queryProjectByName",method = {RequestMethod.POST})
//    @ResponseBody
//    public PageInfo<CostProject> queryProjectByName(CostProject costProject, int currentPageNum, int pageSize)throws Exception{
//        //[pageNum, pageSize]  页码  每页显示数量
//        PageHelper.startPage(currentPageNum,pageSize);
//        PageInfo<CostProject> pageInfo = new PageInfo<>(costProjectMapper.findByName(costProject.getProjectName()));
//        return pageInfo;
//    }
//
//
    @RequestMapping(value = "/check/add",method = {RequestMethod.GET})
    public String addInit(CostCheck costCheck, Model model){
        return "check/check_add";
    }

    @RequestMapping(value = "/check/add",method = {RequestMethod.POST})
    @ResponseBody
    public Result add(CostCheck costCheck){
//        String password = MD5Util.getMD5(SysConstant.INIT_PASSWORD);
//        user.setPassword(password);
        costCheckMapper.insert(costCheck);
        return Result.genSuccessResult();
    }
//
    @RequestMapping(value = "/check/delete",method = {RequestMethod.POST})
    @ResponseBody
    public Result delete(CostCheck costCheck){
        //查询，此用户的角色，将此表t_user_role用户的角色一并删除
        costCheckMapper.deleteByPrimaryKey(costCheck.getCheckId());
        return Result.genSuccessResult();
    }
//
    @RequestMapping(value = "/check/update",method = {RequestMethod.GET})
    public String updateInit(CostCheck costCheck,Model model){
        costCheck = costCheckMapper.findById(costCheck.getCheckId());
        model.addAttribute("costCheck",costCheck);
        return "check/check_update";
    }

    @RequestMapping(value = "/check/update",method = {RequestMethod.POST})
    @ResponseBody
    public Result update(CostCheck costCheck){
        costCheckMapper.updateByPrimaryKey(costCheck);
        return Result.genSuccessResult();
    }


}
