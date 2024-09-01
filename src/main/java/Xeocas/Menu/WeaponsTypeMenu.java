package Xeocas.Menu;

import Xeocas.Factories.Weapons.AK47Factory;
import Xeocas.Factories.Weapons.Kar98Factory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

//second class coming from factory command menu
public class WeaponsTypeMenu extends Menu{

    public WeaponsTypeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Choose a Weapons Factory!";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handelMenu(InventoryClickEvent e) {
        e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
        e.getWhoClicked().closeInventory();
    }

    @Override
    public void setMenuItems() {

        Kar98Factory kar98Factory = new Kar98Factory();
        ItemStack kar = kar98Factory.CreateFactoryBlock();

        AK47Factory ak47Factory = new AK47Factory();
        ItemStack ak47 = ak47Factory.CreateFactoryBlock();

        inventory.setItem(0, kar);
        inventory.setItem(1, ak47);
    }
}
