package com.traveling.mapper;

import com.traveling.entity.SmpUser;
import com.traveling.util.MyMapper;
import org.springframework.stereotype.Repository;

@Repository("smpUserMapper")
public interface SmpUserMapper extends MyMapper<SmpUser> {
}