package com.traveling.service;


import com.traveling.entity.SmpRole;

import java.util.List;

public interface SmpRoleService extends BaseService<SmpRole>{


    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    List<SmpRole> selectRolesByUserId(Integer userid);//根据userid查询所有的角色

}