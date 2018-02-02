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
            //ģ�ⳣ��ҵ�����
            Thread.sleep(1000);
            System.out.println("������:" + value);
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void handleWrite(Lock lock, int index) throws InterruptedException {
        try {
            lock.lock();
            //ģ�ⳣ��ҵ�����
            Thread.sleep(1000);
            System.out.println("д����:" + value);
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
                //�ֱ�ʹ��������������,���ܲ���ֱ�۵ľ����ֳ���,ʹ�ö�д������������Բ���,��ʡ�˴���ʱ��
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
                //�ֱ�ʹ��������������,���ܲ���ֱ�۵ľ����ֳ���
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
