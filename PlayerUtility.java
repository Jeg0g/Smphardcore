package me.jeg0g.smphardcore;

import me.jeg0g.smphardcore.PlayerMemory;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtility {
    private static Map<String, PlayerMemory> playerMemory = new HashMap<>();

    public static PlayerMemory getPlayerMemory(Player p){
        if (!playerMemory.containsKey(p.getUniqueId().toString())){
            PlayerMemory m = new PlayerMemory();
            playerMemory.put(p.getUniqueId().toString(), m);
            return m;
        }
        return playerMemory.get(p.getUniqueId().toString());
    }
    public static void setPlayerMemory(Player p, PlayerMemory memory){
        playerMemory.put(p.getUniqueId().toString(), memory);
    }

//    public static String getFolderPath(Player p){
//        return SmpHardCore.getDataFolder().getAbsolutePath() +"/player/" + p.getUniqueId();
//    }
}
