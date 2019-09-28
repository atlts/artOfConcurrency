package FOUR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Priority {
    public static boolean notStart = true;
    public static  boolean notEnd = true;
    public static class Job implements Runnable{
        private int priority;
        private long jobCount;
        Job(int priority){
            this.priority = priority;
        }
        public void run() {
            jobCount = 0;
            while(notStart){
                Thread.yield();
            }
            while(notEnd){
                Thread.yield();
                jobCount++;
            }
        }
    }

    public static void main(String[] args) throws Exception{
        List<Job> jobList = new ArrayList<Job>();
        for(int i = 0;i < 10;i++){
            int priority = i < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
            Job job = new Job(priority);
            Thread thread = new Thread(job,"Thread :" + i);
            jobList.add(job);
            thread.setPriority(job.priority);
            thread.start();
        }
        notStart = false;
        TimeUnit.SECONDS.sleep(10);
        notEnd = false;
        for(Job job : jobList){
            System.out.println("Job Priority :" + job.priority + "  Job Count : " + job.jobCount);
        }
    }
}

