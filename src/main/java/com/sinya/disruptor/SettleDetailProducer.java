package com.sinya.disruptor;

import com.lmax.disruptor.RingBuffer;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 业务系统调用生产者，将用于结算的业务数据写入生产者消费者共享缓存
 */
public class SettleDetailProducer {

    /**
     * 既然生产者需要将生产的对象放入共享缓存，则此处就需要定义一个共享缓存，此处用disruptor提供的环形缓存
     */
    private RingBuffer<SettleDetail> ringBuffer;

    private static int i = 0;

    public SettleDetailProducer(RingBuffer<SettleDetail> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    public String putSettleDetail(SettleDetail settleDetail) throws InvocationTargetException, IllegalAccessException {
        /**
         * 获取一个可用的序列，该序列应该指向一个空的待放入的传递对象
         */
        long sequence = ringBuffer.next();
        /**
         * 放入一个可用序列，返回一个只创建但未初始化的事件对象
         * 此处为什么能返回一个空对象，推测应该需要个工厂去创建
         * 将业务系统传入的真是对象属性设置到event对象
         */
        SettleDetail event = ringBuffer.get(sequence);
        BeanUtils.copyProperties(event,settleDetail);
        /**
         * 此方法为发布对象的入口方法
         * 发现放入的不是对象，而是一个sequence
         * 推测调用此方法之前应该先获取一个可用的序列
         */
        try {
            i++;
            if (i == 10) {
                throw new RuntimeException("抛异常了。。。");
            }
        }finally {
            ringBuffer.publish(sequence);
        }

        return settleDetail.getIdentity();
    }

}
