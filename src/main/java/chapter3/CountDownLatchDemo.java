package chapter3;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author gfy
 * @date 2017/5/5
 */
public class CountDownLatchDemo {

    private final static int THREAD_NUMBER = 10;

    private final static CountDownLatch end = new CountDownLatch(10);

    public static void main(String args[]) throws InterruptedException {
        CountDownTest countDownTest = new CountDownTest();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();

        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < THREAD_NUMBER; i++) {
            executorService.submit(countDownTest);
        }
        //µÈ´ý¼ì²é
        end.await();
        //·¢Éä»ð¼ý
        System.out.println("Fire!");
        executorService.shutdown();
    }


    private static class CountDownTest implements Runnable {
        @Override
        public void run() {

            try {
                Thread.sleep(500 * 1000);
                System.out.println("check complete");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                CountDownLatchDemo.end.countDown();
            }
        }
    }

}


