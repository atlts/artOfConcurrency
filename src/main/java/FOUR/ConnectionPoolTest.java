package FOUR;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPoolTest {
    static ConnectionPool pool = new ConnectionPool(10);
    static CountDownLatch start = new CountDownLatch(1);
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 50;
        end = new CountDownLatch(threadCount);
        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();
        for(int i = 0;i < threadCount;i++){
            Thread thread = new Thread(new ConnectionRunner(got,notGot,count), "ConnectionRunnerThread:     " + i);
            thread.start();
        }
        start.countDown();//此时所有线程中的start.await()方法才会解开阻塞开始执行接下来的代码，保证所有线程同时开始
        end.await();//当所有线程end.countDown()执行完毕才会不阻塞，保证main（）在所有线程结束之后结束
        System.out.println("total invoke: " + (threadCount * count));
        System.out.println("got connection : " + got);
        System.out.println("notGot connection: " + notGot);
        System.out.println("Ration : " + (notGot.doubleValue() / (double)(threadCount * count)));
    }
    static class ConnectionRunner implements Runnable{
        AtomicInteger got;
        AtomicInteger notGot;
        int count;
        ConnectionRunner(AtomicInteger got,AtomicInteger notGot,int count){
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }
        @Override
        public void run() {
            try{
                start.await();
            }catch(Exception e){
                e.printStackTrace();
            }
            while(count > 0){
                try{
                    Connection connection = pool.fetchConnection(1000);
                    if(connection != null){
                        try{
                            connection.createStatement();
                            connection.commit();
                        }finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    }else{
                        notGot.incrementAndGet();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    count--;
                }
            }
            end.countDown();
        }
    }
}
