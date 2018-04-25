package com.traveling.mapper;



import com.traveling.entity.SmpRole;
import com.traveling.util.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("smpRoleMapper")
public interface SmpRoleMapper extends MyMapper<SmpRole> {

    List<SmpRole> selectRolesByUserId(Integer userid);

}