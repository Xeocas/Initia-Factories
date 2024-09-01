package Xeocas.Listeners;

import Xeocas.Factories.Weapons.AK47Factory;
import Xeocas.Factories.Weapons.Kar98Factory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FactoryInteractListener implements Listener {

    private final JavaPlugin plugin;
    private FileConfiguration dataConfig;
    private File dataFile;
    private final Map<String, String> blockMetadataMap = new HashMap<>();

    // Constants for display names
    private static final String KAR98_DISPLAY_NAME = Kar98Factory.CreateFactoryBlock().getItemMeta().getDisplayName();
    private static final String AK47_DISPLAY_NAME = AK47Factory.CreateFactoryBlock().getItemMeta().getDisplayName();

    public FactoryInteractListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "factories.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadMetadata();
        // Schedule reapplyMetadata to run after server startup
        plugin.getServer().getScheduler().runTask(plugin, this::reapplyMetadata);
    }

    @EventHandler
    public void onFactoryPlaced(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        Block block = event.getBlockPlaced();

        if (meta != null && meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            String factoryType = "";

            if (displayName.equals(KAR98_DISPLAY_NAME)) {
                factoryType = "Kar98";
            } else if (displayName.equals(AK47_DISPLAY_NAME)) {
                factoryType = "AK47";
            }

            if (!factoryType.isEmpty()) {
                block.setMetadata("factory_type", new FixedMetadataValue(plugin, factoryType));
                String blockKey = getBlockKey(block);
                blockMetadataMap.put(blockKey, factoryType);
                saveMetadata();
                plugin.getLogger().info("Placed a " + factoryType + " factory block at " + blockKey);
            }
        }
    }

    @EventHandler
    public void onFactoryBroken(BlockBreakEvent event) {
        Block block = event.getBlock();
        String blockKey = getBlockKey(block);

        if (block.hasMetadata("factory_type")) {
            MetadataValue metadataValue = block.getMetadata("factory_type").get(0);
            String factoryType = metadataValue.asString();

            // Remove the metadata from the block
            block.removeMetadata("factory_type", plugin);
            blockMetadataMap.remove(blockKey);
            dataConfig.set("factories." + blockKey, null);
            saveMetadata();

            // Create the correct item based on the factory type
            ItemStack item;
            if ("Kar98".equals(factoryType)) {
                item = Kar98Factory.CreateFactoryBlock();
            } else if ("AK47".equals(factoryType)) {
                item = AK47Factory.CreateFactoryBlock();
            } else {
                item = new ItemStack(Material.AIR); // Fallback to avoid errors
            }

            // Drop the item at the block's location and prevent default block drops
            block.getWorld().dropItemNaturally(block.getLocation(), item);
            event.setDropItems(false); // Prevent default drops

            plugin.getLogger().info("Broken a " + factoryType + " factory block at " + blockKey);
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        // Check if any of the items in the crafting grid are factory blocks
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    if (displayName.equals(KAR98_DISPLAY_NAME) || displayName.equals(AK47_DISPLAY_NAME)) {
                        // Prevent crafting if a factory block is present
                        event.getInventory().setResult(new ItemStack(Material.AIR)); // Cancel the crafting
                        plugin.getLogger().info("Cancelled crafting involving factory block with display name: " + displayName);
                        break;
                    }
                }
            }
        }
    }

    // Method to reapply metadata to blocks on server startup
    public void reapplyMetadata() {
        plugin.getLogger().info("Reapplying metadata to blocks...");

        for (Map.Entry<String, String> entry : blockMetadataMap.entrySet()) {
            String key = entry.getKey();
            String factoryType = entry.getValue();

            // Split the key to get world name and coordinates
            String[] parts = key.split("_");
            if (parts.length == 4) {
                String worldName = parts[0];
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);

                // Get the world and block
                org.bukkit.World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    Block block = world.getBlockAt(x, y, z);

                    // Ensure the block is of the correct type and reapply metadata
                    if ((factoryType.equals("Kar98") && block.getType() == Material.IRON_BLOCK) ||
                            (factoryType.equals("AK47") && block.getType() == Material.GOLD_BLOCK)) {
                        block.setMetadata("factory_type", new FixedMetadataValue(plugin, factoryType));
                        plugin.getLogger().info("Reapplied metadata " + factoryType + " to block at " + key);
                    } else {
                        plugin.getLogger().warning("Skipping block at " + key + " due to incorrect type.");
                    }
                } else {
                    plugin.getLogger().warning("World " + worldName + " not found for block at " + key);
                }
            } else {
                plugin.getLogger().warning("Invalid metadata key format: " + key);
            }
        }
    }

    private String getBlockKey(Block block) {
        return block.getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
    }

    private void saveMetadata() {
        try {
            for (Map.Entry<String, String> entry : blockMetadataMap.entrySet()) {
                dataConfig.set("factories." + entry.getKey(), entry.getValue());
            }
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save metadata to factories.yml!");
            e.printStackTrace();
        }
    }

    private void loadMetadata() {
        if (dataConfig.contains("factories")) {
            for (String key : dataConfig.getConfigurationSection("factories").getKeys(false)) {
                String factoryType = dataConfig.getString("factories." + key);
                blockMetadataMap.put(key, factoryType);
            }
            plugin.getLogger().info("Loaded metadata from factories.yml");
        } else {
            plugin.getLogger().info("No metadata found in factories.yml");
        }
    }
}
