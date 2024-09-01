package Xeocas.Factories.Weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


//going to be the new factory block
public class AK47Factory {

    public static ItemStack CreateFactoryBlock() {

        ItemStack ak47 = new ItemStack(Material.GOLD_BLOCK, 1);
        ItemMeta ak47_meta = ak47.getItemMeta();

        if (ak47_meta != null) {
            ak47_meta.setDisplayName(ChatColor.RED + "ak47 Factory");
            ak47.setItemMeta(ak47_meta);
        }
        return ak47;
    }
}

