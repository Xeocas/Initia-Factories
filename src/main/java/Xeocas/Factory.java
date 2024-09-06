package Xeocas;

import Xeocas.Commands.FactoryMenuCommand;
import Xeocas.Listeners.FactoryInteractListener;
import Xeocas.Listeners.FactoryProcessor;
import Xeocas.Listeners.MenuListener;
import Xeocas.Listeners.MultiblockListener;
import Xeocas.Menu.PlayerMenuUtility;
import me.deecaad.weaponmechanics.WeaponMechanicsLoader;
import me.deecaad.weaponmechanics.weapon.WeaponHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.weapon.WeaponHandler;

import java.util.HashMap;

public final class Factory extends JavaPlugin {

    //hash map to store menu
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {


        // Plugin startup logic
        System.out.println("Factories!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        Plugin weaponMechanicsPlugin = getServer().getPluginManager().getPlugin("WeaponMechanics");
        if (weaponMechanicsPlugin == null) {
            getLogger().warning("WeaponMechanics plugin is not found.");
        } else {
            getLogger().info("WeaponMechanics plugin found: " + weaponMechanicsPlugin.getDescription().getVersion());
        }


        FactoryInteractListener factoryInteractListener = new FactoryInteractListener(this);
        //registers factorylistener for right click
        getServer().getPluginManager().registerEvents(factoryInteractListener, this);

        //registers menulistener
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        //register multiblock listener
        MultiblockListener multiblockListener = new MultiblockListener(this);
        getServer().getPluginManager().registerEvents(multiblockListener, this);

        //registers command to open the menu
        getCommand("factorymenu").setExecutor(new FactoryMenuCommand());

        setupWeaponHandler();

        getServer().getScheduler().runTaskLater(this, factoryInteractListener::reapplyMetadata, 100L);

    }

    private void setupWeaponHandler() {
        try {
            WeaponHandler weaponHandler = WeaponMechanics.getWeaponHandler(); // Directly access the static method
            if (weaponHandler != null) {
                // Initialize the FactoryProcessor with the WeaponHandler and register its events
                MultiblockListener multiblockListener = new MultiblockListener(this);
                FactoryProcessor factoryProcessor = new FactoryProcessor(multiblockListener, weaponHandler);
                getServer().getPluginManager().registerEvents(factoryProcessor, this);
                getLogger().info("FactoryProcessor initialized successfully.");
            } else {
                throw new IllegalStateException("WeaponHandler is null.");
            }
        } catch (Exception e) {
            getLogger().warning("Failed to initialize WeaponHandler: " + e.getMessage());
            retrySetupWeaponHandler();
        }
    }

    private void retrySetupWeaponHandler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                setupWeaponHandler();  // Retry the setup
            }
        }.runTaskLater(this, 20L); // Retry after 1 second
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