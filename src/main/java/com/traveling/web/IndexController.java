package com.traveling.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.traveling.entity.SmpMenu;
import com.traveling.entity.SmpRole;
import com.traveling.service.SmpMenuService;
import com.traveling.service.SmpRoleService;
import com.traveling.vo.UserMenuVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wangdingfeng
 * @version 1.0
 * @create 2018-04-25 18:53
 * @Description 描述
 **/
@Controller
public class IndexController {

    @Resource
    private SmpMenuService tmenuService;

    @RequestMapping("/main")
    public String main(HttpSession session,Integer parentId,Model model) throws Exception {
        //根据当前用户的角色id和父节点id查询所有菜单及子集json
        List<UserMenuVo> userMenu = loadMenuInfo(session,parentId);
        /*if(session == null || session.getAttribute("userAllMenu").toString() == null){
           userMenu = loadMenuInfo(session,parentId);
            session.setAttribute("userAllMenu",userMenu);
        }else{
            userMenu = (List<UserMenuVo>)session.getAttribute("userAllMenu");
        }*/

        model.addAttribute("userMenu",userMenu);
        return "main";
    }

    /**
     * 加载权限菜单
     * @param session
     * @return
     * @throws Exception
     * 这里传入的parentId是1
     */
    public  List<UserMenuVo> loadMenuInfo(HttpSession session, Integer parentId)throws Exception{

        putTmenuOneClassListIntoSession(session);

        SmpRole currentRole=(SmpRole) session.getAttribute("currentRole");
        //根据当前用户的角色id和父节点id查询所有菜单及子集json
        List<UserMenuVo> menu=getAllMenuByParentId(parentId,currentRole.getId());
        //System.out.println(json);
        return menu;
    }

    /**
     * 获取根频道所有菜单信息
     * @param parentId
     * @param roleId
     * @return
     */
    private  List<UserMenuVo> getAllMenuByParentId(Integer parentId, Integer roleId){
        List<UserMenuVo> newUserMenuVos = new ArrayList<>();
        List<UserMenuVo> userMenuVos=this.getMenuByParentId(parentId, roleId);//得到所有一级菜单
        for(UserMenuVo userMenuVo:userMenuVos){
            //判断该节点下时候还有子节点
            Example example=new Example(SmpMenu.class);
            example.or().andEqualTo("pId",userMenuVo.getId());
            //if("true".equals(jsonObject.get("spread").getAsString())){
            if (tmenuService.selectCountByExample(example)==0) {
                continue;
            }else{
                userMenuVo.setChildMenus(getAllMenuJsonArrayByParentId(userMenuVo.getId(),roleId));
            }
            newUserMenuVos.add(userMenuVo);
        }
        return newUserMenuVos;
    }



    //获取根频道下子频道菜单列表集合
    private List<UserMenuVo> getAllMenuJsonArrayByParentId(Integer parentId,Integer roleId){
        List<UserMenuVo> newUserMenuVos = new ArrayList<>();
        List<UserMenuVo> userMenuVos =this.getMenuByParentId(parentId, roleId);
        for(UserMenuVo userMenuVo:userMenuVos){
            Example example=new Example(SmpMenu.class);
            example.or().andEqualTo("pId",userMenuVo.getId());
            if (tmenuService.selectCountByExample(example)==0) {
                newUserMenuVos.add(userMenuVo);
                continue;
            }else{
                //二级或三级菜单
                userMenuVo.setChildMenus(this.getAllMenuJsonArrayByParentId(userMenuVo.getId(),roleId));
            }
            newUserMenuVos.add(userMenuVo);
        }
        return newUserMenuVos;
    }




    /**
     * 根据父节点和用户角色id查询菜单
     * @param parentId
     * @param roleId
     * @return
     */
    private List<UserMenuVo> getMenuByParentId(Integer parentId, Integer roleId){
        //List<Menu> menuList=menuService.findByParentIdAndRoleId(parentId, roleId);

        List<UserMenuVo> userMenuVos =new ArrayList<>();
        //拼接查询条件
        HashMap<String,Object> paraMap=new HashMap<String,Object>();
        paraMap.put("pid",parentId);
        paraMap.put("roleid",roleId);
        List<SmpMenu> menuList=tmenuService.selectByParentIdAndRoleId(paraMap);
        JsonArray jsonArray=new JsonArray();
        for(SmpMenu menu:menuList){
            UserMenuVo userMenuVo =new UserMenuVo();
            userMenuVo.setId( menu.getId());// 节点id
            userMenuVo.setName(menu.getName());// 节点名称
            userMenuVo.setSpread(false);// 不展开
            userMenuVo.setIcon(menu.getIcon());
            if(StringUtils.isNotEmpty(menu.getUrl())){
                userMenuVo.setUrl(menu.getUrl()); // 菜单请求地址
            }
            userMenuVos.add(userMenuVo);

        }
        return userMenuVos;
    }

    private void putTmenuOneClassListIntoSession(HttpSession session){
        //用来在welcome.ftl中获取主菜单列表
        Example example=new Example(SmpMenu.class);
        example.or().andEqualTo("pId",1);
        List<SmpMenu> tmenuOneClassList=tmenuService.selectByExample(example);
        session.setAttribute("tmenuOneClassList", tmenuOneClassList);
    }

}
