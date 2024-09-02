package Xeocas;

import Xeocas.Commands.FactoryMenuCommand;
import Xeocas.Listeners.FactoryInteractListener;
import Xeocas.Listeners.FactoryProcessor;
import Xeocas.Listeners.MenuListener;
import Xeocas.Listeners.MultiblockListener;
import Xeocas.Menu.PlayerMenuUtility;
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

        new BukkitRunnable() {
            @Override
            public void run() {
                waitForWeaponMechanics(multiblockListener);
            }
        }.runTaskLater(this, 20L); // Start checking after 1 tick

        getServer().getScheduler().runTaskLater(this, factoryInteractListener::reapplyMetadata, 100L);

    }

    private void waitForWeaponMechanics(MultiblockListener multiblockListener) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Plugin weaponMechanicsPlugin = getServer().getPluginManager().getPlugin("WeaponMechanics");
                if (weaponMechanicsPlugin == null) {
                    getLogger().warning("WeaponMechanics plugin is not found. Retrying...");
                    waitForWeaponMechanics(multiblockListener); // Retry if WeaponMechanics is not found
                    return;
                }

                if (weaponMechanicsPlugin instanceof WeaponMechanics) {
                    WeaponMechanics weaponMechanics = (WeaponMechanics) weaponMechanicsPlugin;
                    WeaponHandler weaponHandler = weaponMechanics.getWeaponHandler();
                    if (weaponHandler != null) {
                        // Initialize FactoryProcessor with correct parameters
                        FactoryProcessor factoryProcessor = new FactoryProcessor(multiblockListener, weaponHandler);
                        getServer().getPluginManager().registerEvents(factoryProcessor, Factory.this);
                        getLogger().info("FactoryProcessor initialized successfully.");
                    } else {
                        getLogger().warning("WeaponHandler is not available. Retrying...");
                        waitForWeaponMechanics(multiblockListener); // Retry if WeaponHandler is not ready
                    }
                } else {
                    getLogger().warning("WeaponMechanics plugin is not of expected type. Retrying...");
                    waitForWeaponMechanics(multiblockListener); // Retry if WeaponMechanics is not the correct type
                }
            }
        }.runTaskLater(this, 20L); // Retry every 1 tick
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