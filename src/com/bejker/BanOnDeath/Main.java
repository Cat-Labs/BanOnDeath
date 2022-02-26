package com.bejker.BanOnDeath;

import com.bejker.BanOnDeath.events.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;

import java.io.File;
import java.sql.*;
import java.util.Collection;

public class Main extends JavaPlugin implements Listener  {

    //comment to self SHIFT+F5 to run macro

    public static String databaseDir = "plugins/BanOnDeath";
    public static String databaseFileName = "database.db";
    public static String databasePath = databaseDir+"/"+databaseFileName;


    public static String deaths_table = "deaths_list";

    private static Connection DBConn = null;

    /** Creates parent directories if necessary. Then returns file */
    public static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + "/" + filename);
    }

    public static Connection getDBConnection()
    {
        return DBConn;
    }

    public static int getPlayerDeaths(String name)
    {
        int ret = 0;
        Statement stmt = null;
        try {
            stmt = getDBConnection().createStatement();
           // ResultSet rs = stmt.executeQuery("SELECT * FROM "+deaths_table+" WHERE player_name = \""+name+"\";");
            ResultSet rs = stmt.executeQuery("SELECT date FROM "+deaths_table+" WHERE player_name = \""+name+"\";");

            while (rs.next()) {
                //String player_name = rs.getString("player_name");
                //System.out.println(player_name);
                ret++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    private void connectToDatabase()
    {
        getLogger().info("Trying to establish connection to database in path: "+databasePath);

        fileWithDirectoryAssurance(databaseDir,databaseFileName);
        //IF OBJECT_ID('deaths_list') IS NOT NULL
        try
        {
            String url = "jdbc:sqlite:"+databasePath;

            DBConn = DriverManager.getConnection(url);

            getLogger().info("Established connection!");

            Statement stmt = null;

            String query = "CREATE TABLE "+ deaths_table +" (player_name TEXT, UUID TEXT, death_message TEXT,player_ping INTEGER,player_loc TEXT,date TEXT)";

            try {
                    stmt = DBConn.createStatement();
                    stmt.executeQuery(query);
                    getLogger().info("Created table.");

                } catch (SQLException e ) {
                    //getLogger().info("Table "+table_name+" exists.");
                } finally {
                    if (stmt != null) { stmt.close(); }
                }
        }
        catch (SQLException e) {
            throw new Error("Problem: ", e);
        }
    }

    private void closeDatabaseConnection()
    {
        try {
            getLogger().info("Cleaning up DB connection!");
            if (DBConn != null) {
                DBConn.close();
            }
        } catch (SQLException e) {
            getLogger().info(e.getMessage());
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin BanOnDeath enabled.");
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new RunEveryTick() , 0L, 1L);

        connectToDatabase();
    }

    @EventHandler
    public void onServerListPingEvent(ServerListPingEvent event)
    {
       // getLogger().info("aaa");
        getLogger().info("Server pinged on server list screen.");
        getLogger().info(event.getAddress().toString());
        getLogger().info(event.toString());
    }

    @Override
    public void onDisable() {
        closeDatabaseConnection();
        getLogger().info("Plugin BanOnDeath disabled.");
    }


    public static void ProcessPlayerNames(Boolean log)
    {
        Collection<Player> OnlinePlayers = (Collection<Player>) Bukkit.getOnlinePlayers();
        int longest_username_length = 0;
        for(Player player : OnlinePlayers) {
            int len = player.getDisplayName().length();
            if(len > longest_username_length)
                longest_username_length = len;
        }

        for(Player player : OnlinePlayers)
        {
            //original = original + StringUtils.repeat("x", n);
            int n = longest_username_length -player.getDisplayName().length();
            String msg = ChatColor.YELLOW +player.getDisplayName()+" "+ChatColor.RED+getPlayerDeaths(player.getName())+" "+ ChatColor.GRAY + StringUtils.repeat(" ",n)+player.getPing()+" ";
            if(log)
            Bukkit.getLogger().info(msg);

            player.setPlayerListName(msg);
        }
    }

    public static void main(String[] args) {
	// write your code here
    }
}
