package Xeocas.Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

//new item to be implemented later on!
public class SteelItem {

    public ItemStack createSteelItem() {
        ItemStack steel = new ItemStack(Material.IRON_INGOT);
        ItemMeta steelMeta = steel.getItemMeta();


        if(steelMeta != null) {
            steelMeta.setDisplayName("steel");
            steel.setItemMeta(steelMeta);
        }
        return steel;
    }
}
