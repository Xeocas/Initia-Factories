package Xeocas.Factories.Weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Ammo762Factory {

		public static ItemStack CreateFactoryBlock() {

			ItemStack am762 = new ItemStack(Material.IRON_BLOCK, 1);
			ItemMeta am762_meta = am762.getItemMeta();

			if (am762_meta != null) {
				am762_meta.setDisplayName(ChatColor.RED + "7.62 Ammo Factory");
				am762.setItemMeta(am762_meta);
			}
			return am762;
		}
	}
