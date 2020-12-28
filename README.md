### 1. cboot介绍

#### 介绍
springboot + thymeleaf + Layui + shiro + Jwt 实现后台管理平台。

#### 软件架构
软件架构说明


#### 安装教程

1.  创建MySQL数据库cboot,执行cboot.sql
2.  运行spring Tools suite 导入项目跟新maven依赖
3.  配置Application .yml

```
spring:
  profiles:
    active: local
```

对应Application_local.yml,主要修改数据库连接信息。

```
server:
  port: 8080
  tomcat:
    uri-encoding: utf-8
  servlet:
    session:
      timeout: 30m

spring:
  #jdbc配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3307/cboot?useUnicode=true&characterEncoding=UTF-8&useSSL=true&useAffectedRows=true&tinyInt1isBit=false&serverTimezone=GMT%2B8
    username: root
    password: 123456
```





4. 运行CbootApplication.java,等待项目启动之后在浏览器输入http://localhost:8080

​      

​        用户名admin 密码666666



#### 使用说明

1.  本项目使用Layui作为前端页面开发。
2.  本项目使用Java作为后台服务器语言。

#### 界面预览

1.  演示网站：http://106.12.161.76/login
2.  admin/123456（造数据不易，请勿手抖）



### 2. 系统完善

#### 2.1 分页修改



##### 原来的分页：

```

    @RequestMapping(value = "/resources/list",method = {RequestMethod.POST})
    @ResponseBody
    public PageModel list(Resources resources, PageModel<Resources> page)throws Exception{
           page.init();
           List<Resources> list = resourcesServiceImpl.findPageList(page,resources);
           long count = resourcesServiceImpl.findCount(resources);
           page.packData(count,list);
           return page;
    }

```

  

1. 传递参数：数据表数据resources；page 分页信息，主要包括两个字段： currentPageNumber， pageSize，表示当前页号，以及每个分页的记录数目。 

2. 系统通过resourcesServiceImpl.findPageList 返回数据记录，通过resourcesServiceImpl.findCount返回记录总数。

3. 通过page.packData 设置记录总数totalRecords和数据记录recordList，返回到浏览器

4. 浏览器static/templates/resources/resources_list.html接受数据，进行展示

   ```
    ,parseData: function(res){ //res 即为原始返回的数据
                   return {
                       "code": 0, //解析接口状态
                       "msg": "", //解析提示文本
                       "count": res.totalRecords, //解析数据长度
                       "data": res.recordList //解析数据列表
                   };
   ```

   

存在的问题:

resourcesServiceImpl.findPageList(page,resources);   基于Mybtais，需要讲 resources传入xml文件，生成分页sql依据，例如

```
select * from limit start_record ,end_record.
```

从service，dao，xml需要手工编写，繁琐复杂，工作效率低，容易出错。



##### 采用pageHelper进行分页

1. pom.xml文件引入pagehelper：



```
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.10</version>
</dependency>

```





2.修改后的代码

```
    @RequestMapping(value = "/resources/list",method = {RequestMethod.POST})
    @ResponseBody
    public PageModel list(Resources resources , PageModel<Resources> pageModel)throws Exception{
        pageModel.init();//分析数据：pageSize  currentage
        
        //调用pageHelper
        PageHelper.startPage((int)pageModel.getCurrentPageNum(),(int)pageModel.getPageSize());
        
        //业务数据
        PageInfo<Resources> pageInfo =
                new PageInfo<Resources>(this.resourcesServiceImpl.findList(resources));

        //组装数据返回到客户端，使用的为springboot默认的Json包 spring-boot-starter-json 依赖：
        pageModel.packData(pageInfo.getTotal(),pageInfo.getList());
        return pageModel;
    }

```



​        this.resourcesServiceImpl.findList(resources)对应的xml文件如下：

```
  <select id="findList"  parameterType="com.lz.ht.model.Resources" resultMap="baseResultMap">
        select * from  t_resources t where 1=1
                <if test= "id != null"> and id = #{id}</if>
                <if test= "name != null"> and name = #{name}</if>
                <if test= "resKey != null"> and resKey = #{resKey}</if>
                <if test= "resUrl != null"> and resUrl = #{resUrl}</if>
                <if test= "presKey != null"> and presKey = #{presKey}</if>
                <if test= "pName != null"> and pName = #{pName}</if>
                <if test= "sort != null"> and sort = #{sort}</if>
                <if test= "type != null"> and type = #{type}</if>
    </select>
```

可以看出 sql语句并没有加入分页的sql，pageHelper替你做了一切。

