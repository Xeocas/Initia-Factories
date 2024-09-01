package Xeocas.Listeners;

import Xeocas.Factories.Weapons.AK47Factory;
import Xeocas.Factories.Weapons.Kar98Factory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class MultiblockListener implements Listener {

    private final Plugin plugin;
    private static final String KAR98_DISPLAY_NAME = Kar98Factory.CreateFactoryBlock().getItemMeta().getDisplayName();
    private static final String AK47_DISPLAY_NAME = AK47Factory.CreateFactoryBlock().getItemMeta().getDisplayName();



    public MultiblockListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFinalBlockPlaced(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        ItemStack itemInHand = e.getItemInHand();
        ItemMeta meta = itemInHand.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            if (displayName.equals(KAR98_DISPLAY_NAME) || displayName.equals(AK47_DISPLAY_NAME)) {
                block.setMetadata("factory_type", new FixedMetadataValue(plugin, displayName));
            }
        }
    }


    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            Player player = e.getPlayer();

            if (block != null && block.hasMetadata("factory_type")) {
                String factoryType = block.getMetadata("factory_type").get(0).asString();
                player.sendMessage("Factory Type: " + factoryType); // Debug message

                boolean ironPerimeterComplete = checkIronPerimeter(block, player);
                boolean redstoneAreaComplete = checkRedstoneArea(block, player);
                boolean chestPresent = block.getRelative(BlockFace.UP).getType() == Material.CHEST;

                if (ironPerimeterComplete && redstoneAreaComplete && chestPresent) {
                    player.sendMessage("Factory multiblock structure is complete!");
                } else {
                    if (!ironPerimeterComplete) {
                        player.sendMessage("Iron perimeter is not complete.");
                    }
                    if (!redstoneAreaComplete) {
                        player.sendMessage("Redstone area is not complete.");
                    }
                    if (!chestPresent) {
                        player.sendMessage("Chest must be placed on top of the factory block.");
                    }
                }
            }
        }
    }


    // Change method to public
    public boolean checkForMultiblockStructure(Block centerBlock, Player player) {
        // Check if the center block is a valid factory block
        if (!centerBlock.hasMetadata("factory_type")) {
            player.sendMessage("Center block is not a valid factory block.");
            return false;
        }

        String factoryType = centerBlock.getMetadata("factory_type").get(0).asString();
        Material factoryMaterial = getFactoryMaterial(factoryType);
        if (factoryMaterial == null) {
            player.sendMessage("Invalid factory type.");
            return false;
        }

        player.sendMessage("Factory Type: " + factoryType + ", Factory Material: " + factoryMaterial);

        // Check the iron perimeter
        if (!checkIronPerimeter(centerBlock, player)) {
            player.sendMessage("Iron perimeter check failed.");
            return false;
        }

        // Check the redstone area
        if (!checkRedstoneArea(centerBlock, player)) {
            player.sendMessage("Redstone area check failed.");
            return false;
        }

        // Check the chest on top
        Block chestBlock = centerBlock.getRelative(BlockFace.UP);
        if (chestBlock.getType() != Material.CHEST) {
            player.sendMessage("No chest found on top of the factory block.");
            return false;
        }

        player.sendMessage("Multiblock structure is complete.");
        return true;
    }

    private boolean checkIronPerimeter(Block centerBlock, Player player) {
        int radius = 4; // Radius of the 9x9 structure

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == -radius || x == radius || z == -radius || z == radius) {
                    Block b = centerBlock.getRelative(x, 0, z);
                    if (b.getType() != Material.IRON_BLOCK && !b.equals(centerBlock)) {
                        player.sendMessage("Iron perimeter block at " + b.getLocation() + " is not iron.");
                        return false;
                    }
                }
            }
        }

        player.sendMessage("Iron perimeter check passed.");
        return true;
    }

    private boolean checkRedstoneArea(Block centerBlock, Player player) {
        int radius = 3; // Radius of the 7x7 area inside the perimeter

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == -radius || x == radius || z == -radius || z == radius) {
                    // Edge of the 7x7 redstone area
                    Block b = centerBlock.getRelative(x, 0, z);
                    if (b.getType() != Material.REDSTONE_BLOCK) {
                        player.sendMessage("Redstone area block at " + b.getLocation() + " is not redstone.");
                        return false;
                    }
                }
            }
        }

        player.sendMessage("Redstone area check passed.");
        return true;
    }


    private Material getFactoryMaterial(String factoryType) {
        switch (factoryType) {
            case "Kar98":
                return Material.IRON_BLOCK; // Adjust if the material is different
            case "AK47":
                return Material.GOLD_BLOCK; // Adjust if the material is different
            default:
                return null;
        }
    }
}
