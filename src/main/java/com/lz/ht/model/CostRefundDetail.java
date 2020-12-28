package com.lz.ht.model;

import java.io.Serializable;
import lombok.Data;

/**
 * cost_refund_detail
 * @author 
 */
@Data
public class CostRefundDetail implements Serializable {
    private Integer refundDetailId;

    private Integer detailItem;

    private String paySummary;

    private Integer renfundDetailAmount;

    private Integer refundId;

    private static final long serialVersionUID = 1L;
}