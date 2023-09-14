package xyz.refinedev.api.nametag.thread;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.setup.NameTagUpdate;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Project is property of Refine Development © 2021 - 2023
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
@Getter @Log4j2
public class NameTagThread extends Thread {

    private final Queue<NameTagUpdate> updatesQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true;

    private final NameTagHandler handler;
    private final long ticks;

    public NameTagThread(NameTagHandler nameTagHandler, long ticks) {
        super(nameTagHandler.getPlugin().getName() + " - NameTag Thread");

        this.handler = nameTagHandler;
        this.ticks = ticks;
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

    public void stopExecuting() {
        this.running = false;
    }

    private void tick() {
        while (this.updatesQueue.size() > 0) {
            NameTagUpdate pendingUpdate = this.updatesQueue.poll();

            try {
                this.handler.applyUpdate(pendingUpdate);
            } catch (Exception e) {
                log.fatal("[{}] There was an error updating name-tag for {}", handler.getPlugin().getName(), pendingUpdate.getToRefresh());
                log.error(e);
                e.printStackTrace();
            }
        }
    }

}