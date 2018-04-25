package com.traveling.web;

import com.github.pagehelper.PageHelper;
import com.traveling.entity.SmpRole;
import com.traveling.entity.SmpUser;
import com.traveling.entity.SmpUserRole;
import com.traveling.model.JqgridBean;
import com.traveling.model.PageRusult;
import com.traveling.service.SmpRoleService;
import com.traveling.service.SmpUserRoleService;
import com.traveling.service.SmpUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理用户Controller
 * @author zjt
 */
@Controller
@RequestMapping("/admin/user")
public class UserAdminController {

    @Resource
    private SmpUserService userService;

    @Resource
    private SmpRoleService roleService;

    @Resource
    private SmpUserRoleService userRoleService;


    @RequestMapping("/tousermanage")
    @RequiresPermissions(value = {"用户管理"})
    public String tousermanage() {
        return "power/user";
    }

    /**
     * 分页查询用户信息
     */
    @ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> list(JqgridBean jqgridbean
                    /*String userName,@RequestParam(value="page",required=false)Integer page*/
    ) throws Exception {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Object> datamap = new LinkedHashMap<String, Object>();

        Example tuserExample = new Example(SmpUser.class);
        //tuserExample.or().andIdNotEqualTo(1L);
        Example.Criteria criteria = tuserExample.or();
        criteria.andNotEqualTo("userName","admin");

        if (StringUtils.isNotEmpty(jqgridbean.getSearchField())) {
            if ("username".equalsIgnoreCase(jqgridbean.getSearchField())) {
                if ("eq".contentEquals(jqgridbean.getSearchOper())) {
                    criteria.andEqualTo("userName",jqgridbean.getSearchString());
                }
            }
        }

        if(StringUtils.isNotEmpty(jqgridbean.getSidx())&&StringUtils.isNotEmpty(jqgridbean.getSord())){
            tuserExample.setOrderByClause(jqgridbean.getSidx() + " " + jqgridbean.getSord());
        }

        PageHelper.startPage(jqgridbean.getPage(), jqgridbean.getLength());
        List<SmpUser> userList = userService.selectByExample(tuserExample);
        PageRusult<SmpUser> pageRusult =new PageRusult<SmpUser>(userList);


       /* Integer totalrecords = userService.selectCountByExample(tuserExample);//总记录数
        Page pagebean = new Page(jqgridbean.getLength() * ((jqgridbean.getPage() > 0 ? jqgridbean.getPage() : 1) - 1), jqgridbean.getLength(), totalrecords);
        tuserExample.setPage(pagebean);
        tuserExample.setOrderByClause(jqgridbean.getSidx() + " " + jqgridbean.getSord());
        List<SmpUser> userList = userService.selectByExample(tuserExample);*/


        for (SmpUser u : userList) {
            List<SmpRole> roleList = roleService.selectRolesByUserId(u.getId());
            StringBuffer sb = new StringBuffer();
            for (SmpRole r : roleList) {
                sb.append("," + r.getName());
            }
            u.setRoles(sb.toString().replaceFirst(",", ""));
        }

        resultmap.put("currpage", String.valueOf(pageRusult.getPageNum()));
        resultmap.put("totalpages", String.valueOf(pageRusult.getPages()));
        resultmap.put("totalrecords", String.valueOf(pageRusult.getTotal()));
        resultmap.put("datamap", userList);

        return resultmap;
    }


    @ResponseBody
    @RequestMapping(value = "/addupdateuser")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> addupdateuser(SmpUser tuser) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if (tuser.getId() == null) {//新建
                //首先判断用户名是否可用
                Example tuserExample = new Example(SmpUser.class);
                tuserExample.or().andEqualTo("userName",tuser.getUserName());
                List<SmpUser> userlist = userService.selectByExample(tuserExample);
                if (userlist != null && userlist.size() > 0) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "当前用户名已存在");
                    return resultmap;
                }
                userService.saveNotNull(tuser);
            } else {//编辑
                SmpUser oldObject=userService.selectByKey(tuser.getId());
                if(oldObject==null){
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "当前用户名不存在");
                    return resultmap;
                }else{
                    userService.updateNotNull(tuser);
                }
            }
            resultmap.put("state", "success");
            resultmap.put("mesg", "操作成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，系统异常");
            return resultmap;
        }
    }



    @ResponseBody
    @RequestMapping(value = "/deleteuser")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> deleteuser(SmpUser tuser) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if(tuser.getId()!=null&&!tuser.getId().equals(0)){
                SmpUser user=userService.selectByKey(tuser.getId());
                if(user==null){
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "删除失败,无法找到该记录");
                    return resultmap;
                }else{

                    //还需删除用户角色中间表
                    Example tuserroleexample=new Example(SmpUserRole.class);
                    tuserroleexample.or().andEqualTo("userId",tuser.getId());
                    userRoleService.deleteByExample(tuserroleexample);

                    userService.delete(tuser.getId());

                }
            }else{
                resultmap.put("state", "fail");
                resultmap.put("mesg", "删除失败");
            }


            resultmap.put("state", "success");
            resultmap.put("mesg", "删除成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
            return resultmap;
        }
    }




    @ResponseBody
    @RequestMapping(value = "/selectUserById")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> selectUserById(SmpUser tuser) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if(tuser.getId()!=null&&!tuser.getId().equals(0)){
                tuser=userService.selectByKey(tuser.getId());
                if(tuser==null){
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            }else{
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录的id");
                return resultmap;
            }

            List<SmpRole> roleList = roleService.selectRolesByUserId(tuser.getId());
            StringBuffer sb = new StringBuffer();
            for (SmpRole r : roleList) {
                sb.append("," + r.getName());
            }
            tuser.setRoles(sb.toString().replaceFirst(",", ""));



            //所有角色
            Example troleExample=new Example(SmpRole.class);
            //troleExample.or().andNameNotEqualTo("管理员");
            List<SmpRole> allrolelist=roleService.selectByExample(troleExample);

            resultmap.put("roleList",roleList);//用户拥有的所有角色


            Iterator<SmpRole> it = allrolelist.iterator();
            while (it.hasNext()) {
                SmpRole temp = it.next();
                for(SmpRole e2:roleList){
                    if(temp.getId().compareTo(e2.getId())==0){
                        it.remove();
                    }
                }
            }

            List<SmpRole> notinrolelist=allrolelist;

            resultmap.put("notinrolelist",notinrolelist);//用户不拥有的角色

            resultmap.put("tuser",tuser);
            resultmap.put("state", "success");
            resultmap.put("mesg", "获取成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "获取失败，系统异常");
            return resultmap;
        }
    }



    //设置用户角色
    @ResponseBody
    @RequestMapping(value = "/saveRoleSet")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> saveRoleSet(Integer[] role,Integer id) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            // 根据用户id删除所有用户角色关联实体
            Example tuserroleexample=new Example(SmpUserRole.class);
            tuserroleexample.or().andEqualTo("userId",id);
            userRoleService.deleteByExample(tuserroleexample);

            if(role!=null && role.length>0){
                for(Integer roleid:role){
                    SmpUserRole tuserrole=new SmpUserRole();
                    tuserrole.setRoleId(roleid);
                    tuserrole.setUserId(id);
                    userRoleService.saveNotNull(tuserrole);
                }
            }

            resultmap.put("state", "success");
            resultmap.put("mesg", "设置成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "设置失败，系统异常");
            return resultmap;
        }
    }










    /**
     * 安全退出
     *
     * @return
     * @throws Exception*/

    @GetMapping("/logout")
    @RequiresPermissions(value = {"安全退出"})
    public String logout() throws Exception {
//		logService.save(new Log(Log.LOGOUT_ACTION,"用户注销"));
        SecurityUtils.getSubject().logout();
        return "redirect:/tologin";
    }



    //跳转到修改密码页面
    @RequestMapping("/toUpdatePassword")
    @RequiresPermissions(value = {"修改密码"})
    public String toUpdatePassword() {
        return "power/updatePassword";
    }




    //修改密码
    @ResponseBody
    @PostMapping("/updatePassword")
    @RequiresPermissions(value = {"修改密码"})
    public Map<String, Object> updatePassword(SmpUser tuser) throws Exception {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {

            if(tuser==null){
                resultmap.put("state", "fail");
                resultmap.put("mesg", "设置失败，缺乏字段信息");
                return resultmap;
            }else{
                if(tuser.getId()!=null
                    &&tuser.getId().intValue()!=0
                        && StringUtils.isNotEmpty(tuser.getUserName())
                            && StringUtils.isNotEmpty(tuser.getOldPassword())
                                && StringUtils.isNotEmpty(tuser.getPassword())){
                    Example userExample=new Example(SmpUser.class);
                    Example.Criteria criteria=userExample.or();
                    criteria.andEqualTo("id",tuser.getId())
                            .andEqualTo("userName",tuser.getUserName())
                            .andEqualTo("password",tuser.getOldPassword());
                    List<SmpUser> tuserList=userService.selectByExample(userExample);
                    if(tuserList==null||tuserList.size()==0){
                        resultmap.put("state", "fail");
                        resultmap.put("mesg", "用户名或密码错误");
                        return resultmap;
                    }else{
                        SmpUser newEntity=tuserList.get(0);
                        newEntity.setPassword(tuser.getPassword());
                        userService.updateNotNull(newEntity);
                    }
                }else{
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "设置失败，缺乏字段信息");
                    return resultmap;
                }
            }

            resultmap.put("state", "success");
            resultmap.put("mesg", "密码修改成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "密码修改失败，系统异常");
            return resultmap;
        }
    }

















}
