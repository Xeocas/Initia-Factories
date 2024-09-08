package Xeocas.Listeners;

import me.deecaad.weaponmechanics.weapon.WeaponHandler;
import me.deecaad.weaponmechanics.weapon.reload.ammo.IAmmoType;
import me.deecaad.weaponmechanics.weapon.reload.ammo.ItemAmmo;
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
import me.deecaad.weaponmechanics.weapon.reload.ammo.Ammo;
import me.deecaad.weaponmechanics.weapon.reload.ammo.AmmoRegistry;

import java.util.HashMap;
import java.util.Map;

public class FactoryProcessor implements Listener {

	private static final long COOLDOWN_TIME = 10 * 1000; // 10 seconds in milliseconds
	private final Map<Block, Long> lastConversionTime = new HashMap<>();
	private final MultiblockListener multiblockListener;
	private final WeaponHandler weaponHandler;

	public FactoryProcessor(MultiblockListener multiblockListener, WeaponHandler weaponHandler) {
		this.multiblockListener = multiblockListener;
		this.weaponHandler = weaponHandler;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			Player player = e.getPlayer();

			if (block != null && block.hasMetadata("factory_type")) {
				player.sendMessage("Factory type metadata found: " + block.getMetadata("factory_type").get(0).asString());

				if (multiblockListener.checkForMultiblockStructure(block, player)) {
					player.sendMessage("Multiblock structure is complete.");
					if (canConvert(block)) {
						convertItemsInChest(block, player);
					} else {
						long remainingCooldown = COOLDOWN_TIME - (System.currentTimeMillis() - lastConversionTime.get(block));
						long minutes = (remainingCooldown / 1000) / 60;
						long seconds = (remainingCooldown / 1000) % 60;
						player.sendMessage("You must wait " + minutes + " minutes and " + seconds + " seconds before converting items again.");
					}
				} else {
					player.sendMessage("Multiblock structure is not complete.");
				}
			}
		}
	}

	private boolean canConvert(Block block) {
		Long lastConversion = lastConversionTime.get(block);
		if (lastConversion == null) {
			return true; // No record of last conversion, so it's allowed
		}
		return (System.currentTimeMillis() - lastConversion) >= COOLDOWN_TIME;
	}

	private void setConversionTime(Block block) {
		lastConversionTime.put(block, System.currentTimeMillis());
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

			// Get the factory type from the block metadata
			String factoryType = centerBlock.getMetadata("factory_type").get(0).asString();

			switch (factoryType) {
				case "Kar98":
					conversionSuccess = handleKar98Conversion(items, inventory, player);
					break;
				case "AK47":
					conversionSuccess = handleAK47Conversion(items, inventory, player);
					break;
				case "Mauser":
					conversionSuccess = handleMauserConversion(items, inventory, player);
					break;
				case "Ammo762":
					conversionSuccess = handleAmmo762Conversion(items, inventory, player);
					break;
				default:
					player.sendMessage("Unknown factory type: " + factoryType);
					break;
			}

			if (conversionSuccess) {
				player.sendMessage("Items successfully converted!");
				setConversionTime(centerBlock);
			} else {
				player.sendMessage("Required items are not present or the multiblock structure is not complete.");
			}
		} else {
			player.sendMessage("There must be a chest on top of the factory block.");
		}
	}

	private boolean handleKar98Conversion(ItemStack[] items, Inventory inventory, Player player) {
		if (itemsContain(items, Material.IRON_INGOT, 3, player) &&
				itemsContain(items, Material.DIAMOND, 1, player) &&
				itemsContain(items, Material.GOLD_INGOT, 1, player)) {

			removeItems(inventory, Material.IRON_INGOT, 3, player);
			removeItems(inventory, Material.DIAMOND, 1, player);
			removeItems(inventory, Material.GOLD_INGOT, 1, player);

			String weaponTitle = weaponHandler.getInfoHandler().getWeaponTitle("Kar98k");
			return generateWeapon(inventory, weaponTitle, player);
		}
		return false;
	}

	private boolean handleAK47Conversion(ItemStack[] items, Inventory inventory, Player player) {
		if (itemsContain(items, Material.IRON_INGOT, 3, player) &&
				itemsContain(items, Material.DIAMOND, 1, player) &&
				itemsContain(items, Material.EMERALD, 1, player)) {

			removeItems(inventory, Material.IRON_INGOT, 3, player);
			removeItems(inventory, Material.DIAMOND, 1, player);
			removeItems(inventory, Material.EMERALD, 1, player);

			String weaponTitle = weaponHandler.getInfoHandler().getWeaponTitle("AK47");
			return generateWeapon(inventory, weaponTitle, player);
		}
		return false;
	}

	private boolean handleMauserConversion(ItemStack[] items, Inventory inventory, Player player) {
		if (itemsContain(items, Material.IRON_INGOT, 5, player) &&
				itemsContain(items, Material.DIAMOND, 4, player) &&
				itemsContain(items, Material.EMERALD, 2, player)) {

			removeItems(inventory, Material.IRON_INGOT, 5, player);
			removeItems(inventory, Material.DIAMOND, 4, player);
			removeItems(inventory, Material.EMERALD, 2, player);

			Ammo ammo = AmmoRegistry.AMMO_REGISTRY.get("mauser");
			if (ammo != null) {
				return generateAmmo(inventory, ammo, player);
			} else {
				player.sendMessage("Mauser ammo not found in AmmoRegistry.");
			}
		}
		return false;
	}

	private boolean handleAmmo762Conversion(ItemStack[] items, Inventory inventory, Player player) {
		if (itemsContain(items, Material.IRON_INGOT, 5, player) &&
				itemsContain(items, Material.DIAMOND, 2, player) &&
				itemsContain(items, Material.EMERALD, 2, player)) {

			removeItems(inventory, Material.IRON_INGOT, 5, player);
			removeItems(inventory, Material.DIAMOND, 2, player);
			removeItems(inventory, Material.EMERALD, 2, player);

			Ammo ammo = AmmoRegistry.AMMO_REGISTRY.get("7,62"); // Match this with the title in your 7,62.yml file

			if (ammo != null) {
				return generateAmmo(inventory, ammo, player);
			} else {
				player.sendMessage("7.62 ammo not found in AmmoRegistry.");
			}
		}
		return false;
	}

	private boolean generateAmmo(Inventory inventory, Ammo ammo, Player player) {
		IAmmoType ammoType = ammo.getType();
		ItemStack ammoItem = null;

		if (ammoType instanceof ItemAmmo) {
			ammoItem = ((ItemAmmo) ammoType).getBulletItem(); // Adjust method based on actual class
		}

		if (ammoItem != null) {
			inventory.addItem(ammoItem);
			player.sendMessage(ammo.getDisplay() + " ammo generated successfully.");
			return true;
		} else {
			player.sendMessage("Failed to generate ammo for " + ammo.getDisplay());
		}
		return false;
	}

	private boolean generateWeapon(Inventory inventory, String weaponTitle, Player player) {
		if (weaponTitle != null) {
			ItemStack weapon = weaponHandler.getInfoHandler().generateWeapon(weaponTitle, 1);
			if (weapon != null) {
				inventory.addItem(weapon);
				player.sendMessage(weaponTitle + " generated successfully.");
				return true;
			} else {
				player.sendMessage("Failed to generate " + weaponTitle + " weapon.");
			}
		} else {
			player.sendMessage(weaponTitle + " weapon title not found.");
		}
		return false;
	}

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
			if (remaining <= 0) break;
		}
	}
}
