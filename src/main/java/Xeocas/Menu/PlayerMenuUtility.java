package Xeocas.Menu;

import org.bukkit.entity.Player;

//utility class, getters and setters don't remember a lot...
public class PlayerMenuUtility {

    private Player owner;

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

}
