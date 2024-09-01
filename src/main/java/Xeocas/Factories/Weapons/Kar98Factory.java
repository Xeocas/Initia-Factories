package Xeocas.Factories.Weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

//going to be the new factory block
public class Kar98Factory {

    public static ItemStack CreateFactoryBlock()

    {
        ItemStack kar = new ItemStack(Material.IRON_BLOCK, 1);
        ItemMeta kar_meta = kar.getItemMeta();

        if(kar_meta != null) {
            kar_meta.setDisplayName(ChatColor.GREEN + "Kar98 Factory");
            kar.setItemMeta(kar_meta);
        }
        return kar;
    }


}
