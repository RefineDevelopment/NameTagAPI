package xyz.refinedev.nametag.setup;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.nametag.NameTagHandler;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class NameTagThread extends Thread {

    private final Map<NameTagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    public NameTagThread(JavaPlugin plugin) {
        super(plugin.getName() + " - NameTags Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {
            Iterator<NameTagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while (pendingUpdatesIterator.hasNext()) {
                NameTagUpdate pendingUpdate = pendingUpdatesIterator.next();

                try {
                    NameTagHandler.getInstance().applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(2 * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}