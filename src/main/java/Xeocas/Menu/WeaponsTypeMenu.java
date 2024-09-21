package Xeocas.Menu;

import Xeocas.Factories.Weapons.*;
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
        return "Choose Factory!";
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

       // AK47Factory ak47Factory = new AK47Factory();
       // ItemStack ak47 = ak47Factory.CreateFactoryBlock();

      //  Ammo762Factory ammo762Factory = new Ammo762Factory();
       // ItemStack ammo762 = ammo762Factory.CreateFactoryBlock();

        AmmoMauserFactory ammoMauserFactory = new AmmoMauserFactory();
        ItemStack ammomauser = ammoMauserFactory.CreateFactoryBlock();

        OilExtractor oilExtractor = new OilExtractor();
        ItemStack extractor = oilExtractor.CreateFactoryBlock();

        inventory.setItem(0, kar);
       // inventory.setItem(1, ak47);
       // inventory.setItem(2, ammo762);
        inventory.setItem(1, ammomauser);
        inventory.setItem(2, extractor);
    }
}
