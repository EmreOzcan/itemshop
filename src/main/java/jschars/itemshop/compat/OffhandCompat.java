package jschars.itemshop.compat;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OffhandCompat {
    public static ItemStack getItemInMainHand(Player player) {
        try {
            return player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError e) {
            return player.getInventory().getItemInHand();
        }
    }
}
