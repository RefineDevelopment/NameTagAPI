package xyz.refinedev.api.nametag.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.update.NameTagUpdate;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * This Project is property of Refine Development.
 * Copyright © 2024, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
@Getter
@Log4j2
public class NameTagThread {

    private final Queue<NameTagUpdate> updatesQueue = new PriorityBlockingQueue<>(11, Comparator.comparingInt(NameTagUpdate::getPriority).reversed());
    private volatile boolean running = true;

    private final NameTagHandler handler;
    private final long ticks;

    // Executor services for scheduling and update processing
    private final ScheduledExecutorService scheduler;
    //private final ExecutorService updateExecutor;

    public NameTagThread(NameTagHandler nameTagHandler, long ticks) {
        this.handler = nameTagHandler;
        this.ticks = ticks;

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Bolt - NameTag")
                .setPriority(Thread.NORM_PRIORITY - 1)
                .setDaemon(true)
                .setUncaughtExceptionHandler((a, e) -> {
                    log.fatal("[{}] There was an error in the Thread {}.", handler.getPlugin().getName(), a.getName());
                    log.error(e);
                })
                .build();

        this.scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
        //this.updateExecutor = Executors.newFixedThreadPool(2, threadFactory);

        // Schedule the periodic task to process updates
        scheduler.scheduleAtFixedRate(this::tick, 0, ticks * 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Submit this update to the queue.
     *
     * @param update {@link NameTagUpdate}
     */
    public void addUpdate(NameTagUpdate update) {
        this.updatesQueue.add(update);
    }

    /**
     * Stop executing this thread.
     */
    public void stopExecuting() {
        this.running = false;
        scheduler.shutdown();
    }

    /**
     * Tick this thread to start running the queued updates.
     */
    private void tick() {
        while (running && !updatesQueue.isEmpty()) {
            NameTagUpdate pendingUpdate = updatesQueue.poll();
            if (pendingUpdate != null) {
                //updateExecutor.submit(() -> {
                    try {
                        pendingUpdate.update(handler);
                    } catch (Exception e) {
                        log.fatal("[{}] There was an error issuing NameTagUpdate.", handler.getPlugin().getName());
                        e.printStackTrace();
                    }
                //});
            }
        }
    }
}
