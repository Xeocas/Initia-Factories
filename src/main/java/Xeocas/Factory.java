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

import java.util.HashMap;

public final class Factory extends JavaPlugin {

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        System.out.println("Factories Initialized");

        Plugin weaponMechanicsPlugin = getServer().getPluginManager().getPlugin("WeaponMechanics");
        if (weaponMechanicsPlugin == null) {
            getLogger().warning("WeaponMechanics plugin is not found.");
        } else {
            getLogger().info("WeaponMechanics plugin found: " + weaponMechanicsPlugin.getDescription().getVersion());
        }

        FactoryInteractListener factoryInteractListener = new FactoryInteractListener(this);
        getServer().getPluginManager().registerEvents(factoryInteractListener, this);

        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        MultiblockListener multiblockListener = new MultiblockListener(this);
        getServer().getPluginManager().registerEvents(multiblockListener, this);

        getCommand("factorymenu").setExecutor(new FactoryMenuCommand());

        setupWeaponHandler();


        new BukkitRunnable() {
            @Override
            public void run() {
                factoryInteractListener.reapplyMetadata();
            }
        }.runTaskTimer(this, 0L, 100L);


    }

    private void setupWeaponHandler() {
        try {
            WeaponHandler weaponHandler = WeaponMechanics.getWeaponHandler();
            if (weaponHandler != null) {
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
                setupWeaponHandler();
            }
        }.runTaskLater(this, 20L);
    }

    @Override
    public void onDisable() {
        System.out.println("Factories Disabled");
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        return playerMenuUtilityMap.computeIfAbsent(p, PlayerMenuUtility::new);
    }
}
