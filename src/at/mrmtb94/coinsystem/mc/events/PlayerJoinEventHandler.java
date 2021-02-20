package at.mrmtb94.coinsystem.mc.events;

import at.mrmtb94.coinsystem.mc.constants.InventoryConstants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerJoinEventHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        ItemStack itemStack = new ItemStack(InventoryConstants.ADD_BANK_ACCOUNT_MATERIAL,InventoryConstants.INVENTORY_ACTIONS_SIZE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(InventoryConstants.OPEN_BANK);
        itemStack.setItemMeta(itemMeta);
        event.getPlayer().getInventory().setItem(9, itemStack);

    }
}
