package com.lz.ht.dao;

import com.lz.ht.model.CostRefundDetail;

public interface CostRefundDetailMapper {
    int deleteByPrimaryKey(Integer refundDetailId);

    int insert(CostRefundDetail record);

    int insertSelective(CostRefundDetail record);

    CostRefundDetail selectByPrimaryKey(Integer refundDetailId);

    int updateByPrimaryKeySelective(CostRefundDetail record);

    int updateByPrimaryKey(CostRefundDetail record);
}