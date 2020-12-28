package com.lz.ht.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * cost_collection
 * @author 
 */
@Data
public class CostCollection implements Serializable {
    //收款ID
    private Integer regisId;
//收款日期
    private Date collectionDate;
//收款方式
    private String collectionMethod;
//收款摘要
    private String collectionSummary;
//收入金额
    private Integer collectionAmount;
//是否开票
    private String billFlag;
//项目ID
    private Integer projectId;

    private static final long serialVersionUID = 1L;

    public Integer getProjectId() {
        return projectId;
    }
}