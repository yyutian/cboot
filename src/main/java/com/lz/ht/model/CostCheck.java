package com.lz.ht.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * cost_check
 * @author 
 */
@Data
public class CostCheck implements Serializable {
    private Integer checkId;

    private String checkName;

    private Date checkDate;

    private String checkSummary;

    private Integer checkIncome;

    private Integer checkOutcome;

    private static final long serialVersionUID = 1L;
}