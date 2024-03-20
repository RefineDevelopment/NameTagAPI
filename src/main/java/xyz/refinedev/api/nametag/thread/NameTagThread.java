package xyz.refinedev.api.nametag.thread;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.update.NameTagUpdate;
import xyz.refinedev.api.nametag.util.collection.CachedSizeConcurrentLinkedQueue;

import java.util.Queue;

/**
 * This Project is property of Refine Development.
 * Copyright © 2024, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
@Getter @Log4j2
public class NameTagThread extends Thread {

    private final Queue<NameTagUpdate> updatesQueue = new CachedSizeConcurrentLinkedQueue<>();
    private volatile boolean running = true;

    private final NameTagHandler handler;
    private final long ticks;

    public NameTagThread(NameTagHandler nameTagHandler, long ticks) {
        super(nameTagHandler.getPlugin().getName() + " - NameTag Thread");

        this.handler = nameTagHandler;
        this.ticks = ticks;
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
     * Tick this thread to start running the queued updates.
     */
    private void tick() {
        while (this.updatesQueue.size() > 0) {
            NameTagUpdate pendingUpdate = this.updatesQueue.poll();

            try {
                pendingUpdate.update(handler);
            } catch (Exception e) {
                log.fatal("[{}] There was an error issuing NameTagUpdate.", handler.getPlugin().getName());
                log.error(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop executing this thread.
     */
    public void stopExecuting() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                this.tick();
                Thread.sleep(ticks * 50);
            } catch (InterruptedException e) {
                this.stopExecuting();
            }
        }
    }
}