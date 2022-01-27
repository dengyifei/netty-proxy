package com.efei.proxy.test;

import com.efei.proxy.common.util.MathUtil;

public class Test {

    public void testMathUtil(){
        for (int i=0;i<100;i++){
            String s = MathUtil.getRandomString(6);
            System.out.println(s);
        }

    }
    public static void main(String[] args) {
        // new Test().testMathUtil();
//        TaskRunner t = new TaskRunner();
//        t.start();
//        t.cancel();
//        System.out.println("222");
    }


}

//class TaskRunner extends Thread {
//
//    @Override
//    public void run() {
//        while (true){
//            System.out.println("xxx");
//            Thread.sleep(5000);
//        }
//
//    }
//
//    public void cancel() {
//        interrupt();
//    }
//
//
//}