package FOUR;

import java.sql.Connection;
import java.util.LinkedList;

public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<Connection>();
    public ConnectionPool(int initialSize){
        if(initialSize > 0){
            for(int i = 0;i < initialSize;i++){
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }
    public void releaseConnection(Connection connection){
        if(connection != null){
            synchronized (pool){
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    public Connection fetchConnection (long mills) throws InterruptedException {
        synchronized (pool) {
            if (mills <= 0) {
                while (pool.isEmpty()) {
                    wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remain = mills;
                /*
                超时同步机制，当wait()被唤醒或者到了时间时都会从等待队列进入同步队列而阻塞，
                当获取锁之后再决定是否跳出循环，不要忘记要用同步锁锁住+
                 */
                while (remain > 0 && pool.isEmpty()) {
                    pool.wait(remain);
                    remain = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}
