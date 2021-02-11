package com.sinya.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 此处应该是定义一个spring单例，实现InitializingBean，然后将disruptor的启动方法放在afterPropertiesSet中
 * 这里用main方法代替
 */
public class SpringSingletonDisruptorInit {

    private static Disruptor<SettleDetail> disruptor;

    private static ExecutorService executorService = new ThreadPoolExecutor(1,10,60*10, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(1024));

    static{
        /**
         * 创建disruptor对象，发现需要如下参数
         * 1.事件工厂,去创建个SettleDetailFactory
         * 2.环形缓存的大小
         * 3.线程池
         * 4.生产者是单例还是多例
         * 5.等待策略
         */
        disruptor = new Disruptor<SettleDetail>(new SettleDetailFactory(), 8, executorService, ProducerType.MULTI,new BlockingWaitStrategy());
        /**
         * 注册事件处理handler，其实就是消费者
         */
        disruptor.handleEventsWith(new SettleDetailConsumer());
        /**
         * 开始监听环形缓存上的事件
         */
        disruptor.start();
    }

    public static void main(String[] args) throws InterruptedException {
        RingBuffer<SettleDetail> ringBuffer = disruptor.getRingBuffer();
        final SettleDetailProducer producer = new SettleDetailProducer(ringBuffer);
        /**
         * 模拟各业务线并发调用生产者接口
         */
        for(int i=0; i<10; i++){
            final int idx = i;
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    List<SettleDetail> list = produceSettDetail(10,idx);
                    for(SettleDetail settleDetail : list){
                        try {
                            producer.putSettleDetail(settleDetail);
                        } catch (InvocationTargetException e) {
                        } catch (IllegalAccessException e) {
                        }
                    }
                }
            });
            thread.start();
        }
    }

    private static List<SettleDetail> produceSettDetail(int size,int business){
        List<SettleDetail> details = new ArrayList<SettleDetail>(size);
        Random random = new Random();
        for(int i=0; i<size; i++){
            SettleDetail settleDetail = new SettleDetail();
            settleDetail.setBusinessCode("business-"+business);
            settleDetail.setAmount(new BigDecimal(random.nextInt()));
            settleDetail.setPayAccount("111-"+business);
            settleDetail.setRecAccount("222-"+business);
            details.add(settleDetail);
        }
        return details;
    }

}
