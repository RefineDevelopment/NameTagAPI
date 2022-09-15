package xyz.refinedev.api.nametag.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.refinedev.api.nametag.NameTagHandler;

@RequiredArgsConstructor
public final class NameTagListener implements Listener {

    private final NameTagHandler instance;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        instance.getLoggedIn().add(event.getPlayer().getUniqueId());

        instance.initiatePlayer(event.getPlayer());
        instance.reloadPlayer(event.getPlayer());
        instance.reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        instance.getLoggedIn().remove(event.getPlayer().getUniqueId());
        instance.getTeamMap().remove(event.getPlayer().getUniqueId());
    }
}