package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.PatientInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientInfoMapper extends BaseMapper<PatientInfo> {
}
