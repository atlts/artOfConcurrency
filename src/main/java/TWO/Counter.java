package TWO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
    private AtomicInteger atomicI = new AtomicInteger(0);
    private int cnt = 0;

    public static void main(String[] args) {
        final Counter cas = new Counter();
        List<Thread> ts = new ArrayList<Thread>(600);
        long start = System.currentTimeMillis();
        for(int j = 0;j < 100;j++){
            Thread t = new Thread(new Runnable() {
                public void run() {
                    for(int i = 0;i < 1000;i++){
                        cas.safeCount();
                        cas.count();
                    }
                }
            });
            ts.add(t);
        }
        for(Thread t : ts){
            t.start();
        }
        for(Thread t : ts){
            try{
                t.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println(cas.cnt);
        System.out.println(cas.atomicI.get());
        System.out.println(System.currentTimeMillis() - start);
    }
    private void safeCount(){
        for(;;){
            int i = atomicI.get();
            boolean suc = atomicI.compareAndSet(i,++i);
            if(suc){
                break;
            }
        }
    }
    private void count(){
        cnt++;
    }
}
