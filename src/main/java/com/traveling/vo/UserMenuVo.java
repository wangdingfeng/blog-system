package com.traveling.vo;


import com.traveling.entity.SmpMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wangdingfeng
 * @Description:
 * @Date: 2018/4/25 21:53
 */
public class UserMenuVo extends SmpMenu{


    //子菜单
    private List<UserMenuVo> childMenus = new ArrayList<>();
    //是否展开
    private boolean spread;


    public List<UserMenuVo> getChildMenus() {
        return childMenus;
    }

    public void setChildMenus(List<UserMenuVo> childMenus) {
        this.childMenus = childMenus;
    }

    public boolean isSpread() {
        return spread;
    }

    public void setSpread(boolean spread) {
        this.spread = spread;
    }
}
