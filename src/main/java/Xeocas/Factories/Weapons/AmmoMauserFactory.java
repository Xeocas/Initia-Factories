package Xeocas.Factories.Weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AmmoMauserFactory {

	public static ItemStack CreateFactoryBlock() {

		ItemStack mauser = new ItemStack(Material.IRON_BLOCK, 1);
		ItemMeta mauser_meta = mauser.getItemMeta();

		if (mauser_meta != null) {
			mauser_meta.setDisplayName(ChatColor.RED + "Mauser Ammo Factory");
			mauser.setItemMeta(mauser_meta);
		}
		return mauser;
	}
}
