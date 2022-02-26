package com.bejker.BanOnDeath.events;

import com.bejker.BanOnDeath.Main;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;
import java.sql.*;
import java.util.Date;

import static com.bejker.BanOnDeath.Main.ProcessPlayerNames;
import static com.bejker.BanOnDeath.Main.getPlayerDeaths;
import static org.bukkit.Bukkit.getLogger;

public class PlayerDeath implements Listener {

    public void LogDeath(PlayerDeathEvent event)
    {
        Player p = (Player)event.getEntity();

        Statement stmt = null;
        Date info_date = new Date(System.currentTimeMillis()+60*60*1000);

        //deaths table schema: (player_name TEXT, UUID TEXT, death_message TEXT,player_ping INTEGER,player_loc TEXT,date TEXT)
      //  String bed_loc = "<destroyed>";

      //  if(p.getBedLocation() != null)
         //   bed_loc = String.valueOf(p.getBedLocation().getX())+" "+String.valueOf(p.getBedLocation().getY())+" "+String.valueOf(p.getBedLocation().getZ());

        String query = "INSERT INTO "+Main.deaths_table+"\n" + "VALUES (\""+p.getName()+"\",\""+p.getUniqueId()+
                "\",\""+event.getDeathMessage()+"\","+p.getPing()+","+"\""+p.getLocation()+"\",\""+info_date.toString()+"\");";

        try {
            stmt = Main.getDBConnection().createStatement();
            stmt.executeQuery(query);
            getLogger().info("Added death.");

        } catch (SQLException e ) {
            //getLogger().info("Table "+table_name+" exists.");
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

       // event.setNewExp(event.getDroppedExp()/2);
       // event.setDroppedExp(0);
        //event.setKeepInventory(true);
        event.setKeepInventory(true);
        event.setKeepLevel(false);
        event.getDrops().clear();
        //dodać bana na 12h
        Player p = (Player)event.getEntity();
        getLogger().info("Player died: "+p.getDisplayName()+" keep inv?:"+event.getKeepInventory()+" keep lvl?:"+event.getKeepLevel());
      //  p.getInventory();
        p.getWorld().strikeLightningEffect(p.getLocation());
        ProcessPlayerNames(false);
       // int ban_hours = 1;
        //add 120 mins(2 hours) to current date
        Date unban_date = new Date(System.currentTimeMillis()+120*60*1000);
        //add an hour bcs I need to convert from utc to utc+1
        Date info_date = new Date(unban_date.getTime()+60*60*1000);
        getLogger().info(String.valueOf(info_date.compareTo(unban_date)));
        String info = "Ban za śmierć. Łączny czas ";
        String time_info = "(UTC to nasz czas -1, czyli trzeba dodać 1h, narazie tak musi być sry)";
        getLogger().info(info+unban_date.toString());
        Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(),info+time_info,unban_date,"server");
        Bukkit.getBanList(BanList.Type.IP).addBan(p.getAddress().getHostName(),info+time_info,unban_date,"server");

        event.setDeathMessage(p.getDisplayName()+" umarł tragicznie po raz "+(Main.getPlayerDeaths(p.getName())+1)+" i wróci "+info_date.toString().replaceAll(" UTC",""));

        p.kickPlayer(info+" Do: "+info_date.toString().replaceAll(" UTC",""));
        LogDeath(event);
    }

}
