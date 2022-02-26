package com.bejker.BanOnDeath.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.bejker.BanOnDeath.Main.ProcessPlayerNames;
import static org.bukkit.Bukkit.getLogger;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getLogger().info("Player joined: "+event.getPlayer().toString());
        ProcessPlayerNames(false);
    }

}
