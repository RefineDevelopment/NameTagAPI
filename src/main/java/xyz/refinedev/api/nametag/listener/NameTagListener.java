package xyz.refinedev.api.nametag.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.api.nametag.NameTagHandler;

@RequiredArgsConstructor
public final class NameTagListener implements Listener {

    private final NameTagHandler handler;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.handler.initiatePlayer(player);
        this.handler.reloadPlayer(player);
        this.handler.reloadOthersFor(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.handler.unloadPlayer(player);
    }
}