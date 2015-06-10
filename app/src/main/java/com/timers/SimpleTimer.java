package com.timers;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleTimer {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void sched(final TimerTask tt, final long delay, final long period) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {
                    executor.execute(tt);
                    try {
                        Thread.sleep(period);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void sched(final TimerTask tt, final long delay, final long period, final SimpleTimerListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        listener.before(delay, period);
                        tt.run();
                        listener.after(delay, period);
                    }
                };

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {
                    executor.execute(task);
                    try {
                        Thread.sleep(period);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void onShutdown() {
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        System.out.println("TERMINO A TRHEAD");
    }
}
