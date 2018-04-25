package com.traveling.mapper;

import com.traveling.entity.SmpMenu;
import com.traveling.util.MyMapper;
import org.springframework.stereotype.Repository;


import java.util.HashMap;
import java.util.List;

@Repository("smpMenuMapper")
public interface SmpMenuMapper extends MyMapper<SmpMenu> {

    List<SmpMenu> selectMenusByRoleId(Integer roleid);

    List<SmpMenu> selectByParentIdAndRoleId(HashMap<String, Object> paraMap);

}