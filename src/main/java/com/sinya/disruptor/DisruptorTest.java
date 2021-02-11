package com.sinya.disruptor;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisruptorTest {



    public static void main(String[] args) throws InterruptedException {
        Disruptor disruptor = new Disruptor(new ApplePickEventFactory(),1024,Executors.newFixedThreadPool(3), ProducerType.SINGLE,new BlockingWaitStrategy());
        disruptor.handleEventsWith(new Consumer(1),new Consumer(2),new Consumer(3));
        disruptor.start();
        RingBuffer ringBuffer = disruptor.getRingBuffer();
        final Producer producer = new Producer(ringBuffer);
        //模拟从数据库中查询出很多苹果
        /*for(int i=0; i<100; i++){
            Apple apple = new Apple();
            apple.setId(i);
            //apple.setWeight(Math.random()+"");
            producer.putApple(apple);
        }*/

        CountDownLatch countDownLatch = new CountDownLatch(10);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i=0; i<10; i++){
            executorService.submit(new Runnable() {
                public void run() {
                    Apple apple = new Apple();
                    apple.weight = Math.random()+"";
                    producer.putApple(apple);
                }
            });
            countDownLatch.countDown();
        }
        countDownLatch.await();
    }

    static class ApplePickEventFactory implements EventFactory<ApplePickEvent> {
        public ApplePickEvent newInstance() {
            return new ApplePickEvent();
        }
    }

    @Data
    static class Apple{
        Integer id;
        String weight;
    }

    /**
     *
     */
    static class ApplePickEvent{

        private Apple apple;

        ApplePickEvent(){
        }

        public Apple getApple() {
            return apple;
        }

        public void setApple(Apple apple) {
            this.apple = apple;
        }
    }

    static class Consumer implements EventHandler<ApplePickEvent> {

        int consumerId;

        Consumer(int consumerId){
            this.consumerId = consumerId;
        }

        public void onEvent(ApplePickEvent event, long sequence, boolean endOfBatch) throws Exception {
            System.out.println("process event apple="+JSON.toJSONString(event)+"sequence="+sequence+"endOfBatch="+endOfBatch+"consumer="+consumerId);
        }
    }

    static class Producer{

        private RingBuffer<ApplePickEvent> ringBuffer;

        public Producer(RingBuffer<ApplePickEvent> buffer){
            this.ringBuffer = buffer;
        }

        public void putApple(Apple apple){
            long emptySlotSeq = ringBuffer.next();
            ApplePickEvent pickEvent = ringBuffer.get(emptySlotSeq);
            pickEvent.setApple(apple);
            ringBuffer.publish(emptySlotSeq);
        }


    }

}
