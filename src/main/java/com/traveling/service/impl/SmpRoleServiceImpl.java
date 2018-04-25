package com.traveling.service.impl;


import com.traveling.entity.SmpRole;
import com.traveling.mapper.SmpRoleMapper;
import com.traveling.service.SmpRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href=""mailto:zhaojt@gtmap.cn></a>
 * @version 1.0, 2017/11/10
 * @description
 */

@Service("smpRoleService")
public class SmpRoleServiceImpl extends BaseServiceImpl<SmpRole> implements SmpRoleService {

    @Autowired
    private SmpRoleMapper smpRoleMapper;

    @Override
    public List<SmpRole> selectRolesByUserId(Integer userid) {
        return smpRoleMapper.selectRolesByUserId(userid);
    }
}
