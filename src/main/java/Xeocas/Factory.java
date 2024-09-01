package Xeocas;

import Xeocas.Commands.FactoryMenuCommand;
import Xeocas.Listeners.FactoryInteractListener;
import Xeocas.Listeners.FactoryProcessor;
import Xeocas.Listeners.MenuListener;
import Xeocas.Listeners.MultiblockListener;
import Xeocas.Menu.PlayerMenuUtility;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Factory extends JavaPlugin {

    //hash map to store menu
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Factories!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        FactoryInteractListener factoryInteractListener = new FactoryInteractListener(this);
        //registers factorylistener for right click
        getServer().getPluginManager().registerEvents(factoryInteractListener, this);

        //registers menulistener
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        //register multiblock listener
        MultiblockListener multiblockListener = new MultiblockListener(this);
        getServer().getPluginManager().registerEvents(multiblockListener, this);

        FactoryProcessor factoryProcessor = new FactoryProcessor(multiblockListener, this);
        getServer().getPluginManager().registerEvents(factoryProcessor, this);

        //registers command to open the menu
        getCommand("factorymenu").setExecutor(new FactoryMenuCommand());

        getServer().getScheduler().runTaskLater(this, factoryInteractListener::reapplyMetadata, 100L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Factories over");
    }

    //menu utility
    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;

        if(playerMenuUtilityMap.containsKey(p)){
            return playerMenuUtilityMap.get(p);
        }
        else {
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        }
    }

}
