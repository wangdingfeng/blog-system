package com.traveling.service;


import com.traveling.entity.SmpMenu;

import java.util.HashMap;
import java.util.List;

public interface SmpMenuService extends BaseService<SmpMenu>{

    List<SmpMenu> selectMenusByRoleId(Integer roleid);

    List<SmpMenu> selectByParentIdAndRoleId(HashMap<String, Object> paraMap);


}