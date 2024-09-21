package Xeocas.Factories.Weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


//going to be the new factory block
public class OilExtractor {

	public static ItemStack CreateFactoryBlock() {

		ItemStack extractor = new ItemStack(Material.IRON_BLOCK, 1);
		ItemMeta extractor_meta = extractor.getItemMeta();

		if (extractor_meta != null) {
			extractor_meta.setDisplayName(ChatColor.AQUA + "Oil Extractor");
			extractor.setItemMeta(extractor_meta);
		}
		return extractor;
	}
}

