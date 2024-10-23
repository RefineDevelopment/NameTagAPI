package xyz.refinedev.api.nametag.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.tablist.util.GlitchFixEvent;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2023, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 12/3/2023
 */
@RequiredArgsConstructor
public class GlitchFixListener implements Listener {

    private final NameTagHandler nameTagHandler;

    @EventHandler
    public void onGlitch(GlitchFixEvent event) {
        Player player = event.getPlayer();
        nameTagHandler.reloadPlayer(player);
    }

}
