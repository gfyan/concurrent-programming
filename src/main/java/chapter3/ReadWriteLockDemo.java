package chapter3;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author gfy
 * @date 2017/5/5
 */
public class ReadWriteLockDemo {
    private static Lock lock = new ReentrantLock();
    private static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private static Lock readLock = reentrantReadWriteLock.readLock();
    private static Lock writeLock = reentrantReadWriteLock.writeLock();
    private int value;

    public Object handleRead(Lock lock) throws InterruptedException {
        try {
            lock.lock();
            //模拟常规业务操作
            Thread.sleep(1000);
            System.out.println("读操作:" + value);
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void handleWrite(Lock lock, int index) throws InterruptedException {
        try {
            lock.lock();
            //模拟常规业务操作
            Thread.sleep(1000);
            System.out.println("写操作:" + value);
            value = index;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String args[]) {
        final ReadWriteLockDemo demo = new ReadWriteLockDemo();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().
                setNameFormat("demo-pool-%d").build();

        ExecutorService executorService = new ThreadPoolExecutor(38, 38, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2), threadFactory, new ThreadPoolExecutor.AbortPolicy());


        Runnable readRunnable = new Runnable() {
            @Override
            public void run() {
                //分别使用两种锁来运行,性能差别很直观的就体现出来,使用读写锁后读操作可以并行,节省了大量时间
                try {
                    /**demo.handleRead(lock);**/
                    demo.handleRead(readLock);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        Runnable writeRunnable = new Runnable() {
            @Override
            public void run() {
                //分别使用两种锁来运行,性能差别很直观的就体现出来
                try {
                    /**demo.handleWrite(lock, new Random().nextInt(100));**/
                    demo.handleWrite(writeLock, new Random().nextInt(100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        for (int i = 0; i < 1000; i++) {
            executorService.submit(readRunnable);
        }
        for (int i = 18; i < 20; i++) {
            executorService.submit(writeRunnable);
        }


        executorService.shutdown();
    }
}
