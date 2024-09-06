package Xeocas.Listeners;

import me.deecaad.weaponmechanics.weapon.WeaponHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class FactoryProcessor implements Listener {

	private static final long COOLDOWN_TIME = 10 * 1000; // 5 minutes in milliseconds
	private final Map<Player, Long> lastConversionTime = new HashMap<>();
	private final MultiblockListener multiblockListener;
	private final WeaponHandler weaponHandler;

	public FactoryProcessor(MultiblockListener multiblockListener, WeaponHandler weaponHandler) {
		this.multiblockListener = multiblockListener;
		this.weaponHandler = weaponHandler;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) { // Use the Action enum from org.bukkit.event.block.Action
			Block block = e.getClickedBlock();
			Player player = e.getPlayer();

			if (block != null && block.hasMetadata("factory_type")) {
				player.sendMessage("Factory type metadata found: " + block.getMetadata("factory_type").get(0).asString());

				if (multiblockListener.checkForMultiblockStructure(block, player)) {
					player.sendMessage("Multiblock structure is complete.");
					if (canConvert(player)) {
						convertItemsInChest(block, player);
					} else {
						long remainingCooldown = COOLDOWN_TIME - (System.currentTimeMillis() - lastConversionTime.get(player));
						long minutes = (remainingCooldown / 1000) / 60;
						long seconds = (remainingCooldown / 1000) % 60;
						player.sendMessage("You must wait " + minutes + " minutes and " + seconds + " seconds before converting items again.");
					}
				} else {
					player.sendMessage("Multiblock structure is not complete.");
				}
			} else {
				player.sendMessage("No factory type metadata found on block.");
			}
		}
	}

	private boolean canConvert(Player player) {
		Long lastConversion = lastConversionTime.get(player);
		if (lastConversion == null) {
			return true; // No record of last conversion, so it's allowed
		}
		return (System.currentTimeMillis() - lastConversion) >= COOLDOWN_TIME;
	}

	private void setConversionTime(Player player) {
		lastConversionTime.put(player, System.currentTimeMillis());
	}

	private void convertItemsInChest(Block centerBlock, Player player) {
		Block chestBlock = centerBlock.getRelative(BlockFace.UP);
		if (chestBlock.getType() == Material.CHEST) {
			Inventory inventory = ((org.bukkit.block.Chest) chestBlock.getState()).getInventory();
			ItemStack[] items = inventory.getContents();
			boolean conversionSuccess = false;

			// Debug message to show the items in the chest
			player.sendMessage("Items in chest:");
			for (ItemStack item : items) {
				if (item != null) {
					player.sendMessage(item.getType() + ": " + item.getAmount());
				}
			}

			// Check for Kar98k factory conversion
			if (itemsContain(items, Material.IRON_INGOT, 3, player) &&
					itemsContain(items, Material.DIAMOND, 1, player) &&
					itemsContain(items, Material.GOLD_INGOT, 1, player)) {

				// Conversion for Kar98k factory
				removeItems(inventory, Material.IRON_INGOT, 3, player);
				removeItems(inventory, Material.DIAMOND, 1, player);
				removeItems(inventory, Material.GOLD_INGOT, 1, player);

				String weaponTitle = weaponHandler.getInfoHandler().getWeaponTitle("Kar98k");
				if (weaponTitle != null) {
					ItemStack kar98kWeapon = weaponHandler.getInfoHandler().generateWeapon(weaponTitle, 1);
					if (kar98kWeapon != null) {
						inventory.addItem(kar98kWeapon);
						conversionSuccess = true;
						player.sendMessage("Kar98k generated successfully.");
					} else {
						player.sendMessage("Failed to generate Kar98k weapon.");
					}
				} else {
					player.sendMessage("Kar98k weapon title not found.");
				}

			}
			// Check for AK47 factory conversion
			else if (itemsContain(items, Material.IRON_INGOT, 3, player) &&
					itemsContain(items, Material.DIAMOND, 1, player) &&
					itemsContain(items, Material.EMERALD, 1, player)) {

				// Conversion for AK47 factory
				removeItems(inventory, Material.IRON_INGOT, 3, player);
				removeItems(inventory, Material.DIAMOND, 1, player);
				removeItems(inventory, Material.EMERALD, 1, player);

				String weaponTitle = weaponHandler.getInfoHandler().getWeaponTitle("AK47");
				if (weaponTitle != null) {
					ItemStack ak47Weapon = weaponHandler.getInfoHandler().generateWeapon(weaponTitle, 1);
					if (ak47Weapon != null) {
						inventory.addItem(ak47Weapon);
						conversionSuccess = true;
						player.sendMessage("AK47 generated successfully.");
					} else {
						player.sendMessage("Failed to generate AK47 weapon.");
					}
				} else {
					player.sendMessage("AK47 weapon title not found.");
				}

			}

			if (conversionSuccess) {
				player.sendMessage("Items successfully converted!");
				setConversionTime(player);
			} else {
				player.sendMessage("Required items are not present or the multiblock structure is not complete.");
			}
		} else {
			player.sendMessage("There must be a chest on top of the factory block.");
		}
	}


	// Updated item check with debug messages
	private boolean itemsContain(ItemStack[] items, Material material, int count, Player player) {
		int total = 0;
		for (ItemStack item : items) {
			if (item != null && item.getType() == material) {
				total += item.getAmount();
			}
		}
		player.sendMessage("Total " + material + " found: " + total + " (required: " + count + ")");
		return total >= count;
	}

	private void removeItems(Inventory inventory, Material material, int count, Player player) {
		int remaining = count;
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() == material) {
				player.sendMessage("Removing " + Math.min(item.getAmount(), remaining) + " " + material + " from inventory.");
				if (item.getAmount() <= remaining) {
					remaining -= item.getAmount();
					inventory.remove(item);
				} else {
					item.setAmount(item.getAmount() - remaining);
					remaining = 0;
					break;
				}
			}
			// Stop removing if all required items are removed
			if (remaining <= 0) break;
		}
	}
}
