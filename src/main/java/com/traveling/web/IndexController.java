package com.traveling.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.traveling.entity.SmpMenu;
import com.traveling.entity.SmpRole;
import com.traveling.service.SmpMenuService;
import com.traveling.service.SmpRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
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
        JsonObject userMenu = loadMenuInfo(session,parentId);
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
    public JsonObject loadMenuInfo(HttpSession session, Integer parentId)throws Exception{

        putTmenuOneClassListIntoSession(session);

        SmpRole currentRole=(SmpRole) session.getAttribute("currentRole");
        //根据当前用户的角色id和父节点id查询所有菜单及子集json
        JsonObject json=getAllMenuByParentId(parentId,currentRole.getId());
        //System.out.println(json);
        return json;
    }

    /**
     * 获取根频道所有菜单信息
     * @param parentId
     * @param roleId
     * @return
     */
    private JsonObject getAllMenuByParentId(Integer parentId, Integer roleId){
        JsonObject resultObject=new JsonObject();
        JsonArray jsonArray=this.getMenuByParentId(parentId, roleId);//得到所有一级菜单
        for(int i=0;i<jsonArray.size();i++){
            JsonObject jsonObject=(JsonObject) jsonArray.get(i);
            //判断该节点下时候还有子节点
            Example example=new Example(SmpMenu.class);
            example.or().andEqualTo("pId",jsonObject.get("id").getAsString());
            //if("true".equals(jsonObject.get("spread").getAsString())){
            if (tmenuService.selectCountByExample(example)==0) {
                continue;
            }else{
                //由于后台模板的规定，一级菜单以title最为json的key
                resultObject.add(jsonObject.get("title").getAsString(), getAllMenuJsonArrayByParentId(jsonObject.get("id").getAsInt(),roleId));
            }
        }
        return resultObject;
    }



    //获取根频道下子频道菜单列表集合
    private JsonArray getAllMenuJsonArrayByParentId(Integer parentId,Integer roleId){
        JsonArray jsonArray=this.getMenuByParentId(parentId, roleId);
        for(int i=0;i<jsonArray.size();i++){
            JsonObject jsonObject=(JsonObject) jsonArray.get(i);
            //判断该节点下是否还有子节点
            Example example=new Example(SmpMenu.class);
            example.or().andEqualTo("pId",jsonObject.get("id").getAsString());
            //if("true".equals(jsonObject.get("spread").getAsString())){
            if (tmenuService.selectCountByExample(example)==0) {
                continue;
            }else{
                //二级或三级菜单
                jsonObject.add("children", getAllMenuJsonArrayByParentId(jsonObject.get("id").getAsInt(),roleId));
            }
        }
        return jsonArray;
    }




    /**
     * 根据父节点和用户角色id查询菜单
     * @param parentId
     * @param roleId
     * @return
     */
    private JsonArray getMenuByParentId(Integer parentId,Integer roleId){
        //List<Menu> menuList=menuService.findByParentIdAndRoleId(parentId, roleId);
        HashMap<String,Object> paraMap=new HashMap<String,Object>();
        paraMap.put("pid",parentId);
        paraMap.put("roleid",roleId);
        List<SmpMenu> menuList=tmenuService.selectByParentIdAndRoleId(paraMap);
        JsonArray jsonArray=new JsonArray();
        for(SmpMenu menu:menuList){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("id", menu.getId()); // 节点id
            jsonObject.addProperty("title", menu.getName()); // 节点名称
            jsonObject.addProperty("spread", false); // 不展开
            jsonObject.addProperty("icon", menu.getIcon());
            if(StringUtils.isNotEmpty(menu.getUrl())){
                jsonObject.addProperty("href", menu.getUrl()); // 菜单请求地址
            }
            jsonArray.add(jsonObject);

        }
        return jsonArray;
    }

    private void putTmenuOneClassListIntoSession(HttpSession session){
        //用来在welcome.ftl中获取主菜单列表
        Example example=new Example(SmpMenu.class);
        example.or().andEqualTo("pId",1);
        List<SmpMenu> tmenuOneClassList=tmenuService.selectByExample(example);
        session.setAttribute("tmenuOneClassList", tmenuOneClassList);
    }

}
