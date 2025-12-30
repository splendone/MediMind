package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.Medication;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MedicationMapper extends BaseMapper<Medication> {
}
