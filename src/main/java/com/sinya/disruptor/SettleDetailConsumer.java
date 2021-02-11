package com.sinya.disruptor;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.EventHandler;

/**
 * 结算系统消费者调用，从生产者消费者共享缓存取出业务数据进行结算
 * 1.需继承EventHandler，onEvent为生产消费协调者即Disruptor在生产者写入缓存后触发事件由协调者调用消费者的事件处理回调函数
 */
public class SettleDetailConsumer implements EventHandler<SettleDetail> {

    private SettleService settleService = new SettleService();

    public void onEvent(SettleDetail event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(String.format("开始消费：[%s] %s",sequence, JSON.toJSONString(event)));

        /**
         * 真是消费逻辑，此处应该是结算逻辑
         */
        settleService.settle(event);

        System.out.println(String.format("结束消费：[%s] %s",sequence, JSON.toJSONString(event)));
        System.out.println("-------------------------");
    }

    class SettleService{
        public void settle(SettleDetail settleDetail){
            System.out.println("去结算了。。。。");
        }
    }
}
