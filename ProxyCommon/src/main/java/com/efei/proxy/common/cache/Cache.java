package com.efei.proxy.common.cache;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存类
 */
public class Cache {

    public final static HashMap<String,Entity> map = new HashMap<>();

    public final static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    public  synchronized  static void put(String key,Object value){
        Cache.put(key,value,0);
    }

    public  synchronized  static void put(String key,Object value,long expired){
        //清除原键值对
        Cache.remove(key);
        //设置过期时间
        if (expired > 0) {
            Future future = scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    //过期后清除该键值对
                    synchronized (Cache.class) {
                        map.remove(key);
                    }
                }
            }, expired, TimeUnit.MILLISECONDS);
            map.put(key, new Entity(value, future));
        } else {
            //不设置过期时间
            map.put(key, new Entity(value, null));
        }
    }

    public  synchronized  static <T> T remove(String key){
        //清除原缓存数据
        Entity entity = map.remove(key);
        if (entity == null) {
            return null;
        }
        //清除原键值对定时器
        if (entity.future != null) {
            entity.future.cancel(true);
        }
        return (T) entity.value;
    }

    public  synchronized  static void update(String key,Object value,long expired){
        //清除原键值对
        Cache.remove(key);
        Cache.put(key,value,expired);
    }

    public  synchronized   static <T> T get(String key){
        Entity e = map.get(key);
        return e == null ? null : (T) e.value;
    }

    public synchronized static int size() {
        return map.size();
    }

    public static void shutdown(){
        if(!scheduledExecutorService.isShutdown()){

        }
        scheduledExecutorService.shutdown();
    }

    private static class Entity {
        public Object value;
        public Future future;

        public Entity(Object value, Future future) {
            this.value = value;
            this.future = future;
        }
    }

    //-----------------------

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        String key = "id";
        //不设置过期时间
        System.out.println("***********不设置过期时间**********");
        Cache.put(key, 123);
        System.out.println("key:" + key + ", value:" + Cache.get(key));
        System.out.println("key:" + key + ", value:" + Cache.remove(key));
        System.out.println("key:" + key + ", value:" + Cache.get(key));

        //设置过期时间
        System.out.println("\n***********设置过期时间**********");
        Cache.put(key, "123456", 1000);
        System.out.println("key:" + key + ", value:" + Cache.get(key));
        Thread.sleep(2000);
        System.out.println("key:" + key + ", value:" + Cache.get(key));

        System.out.println("\n***********100w读写性能测试************");
        //创建有10个线程的线程池，将1000000次操作分10次添加到线程池
        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        //每批操作数量
        int batchSize = 100000;

        //添加
        {
            CountDownLatch latch = new CountDownLatch(threads);
            AtomicInteger n = new AtomicInteger(0);
            long start = System.currentTimeMillis();

            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < batchSize; i++) {
                        int value = n.incrementAndGet();
                        Cache.put(key + value, value, 10000);
                    }
                    latch.countDown();
                });
            }
            //等待全部线程执行完成，打印执行时间
            latch.await();
            System.out.printf("添加耗时：%dms\n", System.currentTimeMillis() - start);
        }

        //查询
        {
            CountDownLatch latch = new CountDownLatch(threads);
            AtomicInteger n = new AtomicInteger(0);
            long start = System.currentTimeMillis();
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < batchSize; i++) {
                        int value = n.incrementAndGet();
                        Cache.get(key + value);
                    }
                    latch.countDown();
                });
            }
            //等待全部线程执行完成，打印执行时间
            latch.await();
            System.out.printf("查询耗时：%dms\n", System.currentTimeMillis() - start);
        }

        System.out.println("当前缓存容量：" + Cache.size());
        pool.shutdown();
        Cache.shutdown();
    }
}
