package com.sinya.disruptor;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 需要结算的业务数据
 * 也是生产者消费者传递的对象，我们暂用《传递对象》称呼。
 */
@Data
public class SettleDetail {
    //业务线代码
    private String businessCode;
    //同一个业务线可以通过identity区分结算明细
    private String identity;
    //付款账户
    private String payAccount;
    //收款账户
    private String recAccount;
    //结算金额
    private BigDecimal amount;
}
