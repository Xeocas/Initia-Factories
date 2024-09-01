package Xeocas.Commands;

import Xeocas.Factory;
import Xeocas.Menu.WeaponsTypeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

//Opens the Factory Menu
public class FactoryMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p) {
            new WeaponsTypeMenu(Factory.getPlayerMenuUtility(p)).open();

            System.out.println("Menu Opened");


        }
        //Disallows console use
        else if(sender instanceof ConsoleCommandSender) {

            System.out.println("The command was ran through the console.");
        }

        return true;
    }
}
