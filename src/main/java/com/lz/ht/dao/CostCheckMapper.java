package com.lz.ht.dao;

import com.lz.ht.model.CostCheck;
import com.lz.ht.model.CostProject;

import java.util.List;

public interface CostCheckMapper {
    int deleteByPrimaryKey(Integer checkId);

    int insert(CostCheck record);

    int insertSelective(CostCheck record);

    CostCheck selectByPrimaryKey(Integer checkId);

    int updateByPrimaryKeySelective(CostCheck record);

    int updateByPrimaryKey(CostCheck record);

    List<CostCheck> findAll();

    CostCheck findById(Integer check);
}