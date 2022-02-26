package com.bejker.BanOnDeath;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import static com.bejker.BanOnDeath.Main.ProcessPlayerNames;

public class RunEveryTick implements Runnable{

    private int counter = 0;



    @Override
    public void run() {
        counter++;
        //update ping
        if(counter > 200) //100 ticks = 5s
        {
            Scoreboard blankBoard = (Scoreboard) Bukkit.getScoreboardManager().getMainScoreboard();
           // getLogger().info("Update now!");


            //getLogger().info("Online players:");
            ProcessPlayerNames(false);
            counter = 0;
            
        }
    }
}
