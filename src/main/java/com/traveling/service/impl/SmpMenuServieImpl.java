package com.traveling.service.impl;


import com.traveling.entity.SmpMenu;
import com.traveling.mapper.SmpMenuMapper;
import com.traveling.service.SmpMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author wandgingfeng
 * @version 1.0,
 * @date 2018/04/23
 * @description
 */
@Service("smpMenuServie")
public class SmpMenuServieImpl extends BaseServiceImpl<SmpMenu> implements SmpMenuService {

    @Autowired
    private SmpMenuMapper smpMenuMapper;

    @Override
    public List<SmpMenu> selectMenusByRoleId(Integer roleid) {
        return smpMenuMapper.selectMenusByRoleId(roleid);
    }

    @Override
    public List<SmpMenu> selectByParentIdAndRoleId(HashMap<String, Object> paraMap) {
        return smpMenuMapper.selectByParentIdAndRoleId(paraMap);
    }
}
