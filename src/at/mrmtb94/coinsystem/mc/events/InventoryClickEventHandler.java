package at.mrmtb94.coinsystem.mc.events;

import at.mrmtb94.coinsystem.Coinsystem;
import at.mrmtb94.coinsystem.db.entities.AccountEntity;
import at.mrmtb94.coinsystem.db.entities.PlayerEntity;
import at.mrmtb94.coinsystem.mc.constants.InventoryConstants;
import at.mrmtb94.coinsystem.mc.utils.InventoryUtils;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;

public class InventoryClickEventHandler implements Listener {

    private Coinsystem coinsystem;
    private Gson gson;
    public InventoryClickEventHandler(Coinsystem coinsystem){

        this.coinsystem = coinsystem;
        this.gson = new Gson();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        try {
            if (event.getCurrentItem() != null && event.getAction().equals(InventoryAction.PICKUP_ALL)) {

                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(InventoryConstants.OPEN_BANK)
                        && event.getCurrentItem().getType().equals(InventoryConstants.ADD_BANK_ACCOUNT_MATERIAL)) {
                    showPlayerBankAccounts((Player) event.getWhoClicked());
                    event.setCancelled(true);
                }
                if (event.getView().getTitle().equals(InventoryConstants.BANK_ACCOUNTS_INV)) {
                    handleBankAccountsInventory(event);
                    event.setCancelled(true);
                }
                if (event.getView().getTitle().startsWith(InventoryConstants.BANK_ACCOUNT_INV)) {
                    handleBankAccountInventory(event);
                    event.setCancelled(true);
                }
                event.setCancelled(true);
            }
        } catch(Exception e){
            event.setCancelled(true);
        }

    }


    private void showPlayerBankAccounts(Player player){
        PlayerEntity playerEntity = this.coinsystem.getPlayerService().getPlayerBank(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(player, InventoryConstants.INVENTORY_SIZE, InventoryConstants.BANK_ACCOUNTS_INV);
        for(AccountEntity accountEntity : playerEntity.getAccounts()){
            inventory.addItem(InventoryUtils.createNewItemStack(InventoryConstants.BANK_ACCOUNT_MATERIAL,
                    InventoryConstants.INVENTORY_ACTIONS_SIZE,
                    accountEntity.getAccountName(),
                    Arrays.asList("Balance: "+accountEntity.getBalance(),"Transactions: "+accountEntity.getTransactions().size()),
                    Map.of("playerBankData", accountEntity),
                    this.coinsystem));
        }
        inventory.setItem(InventoryConstants.ADD_BANK_ACCOUNT_POSITION, InventoryUtils.createNewItemStack(InventoryConstants.ADD_BANK_ACCOUNT_MATERIAL,
                InventoryConstants.INVENTORY_ACTIONS_SIZE,
                InventoryConstants.ADD_BANK_ACCOUNT_TEXT,
                this.coinsystem));
        player.openInventory(inventory);
    }



    private void handleBankAccountsInventory(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        Material itemMaterial = itemStack.getType();

        if(itemMaterial.equals(InventoryConstants.ADD_BANK_ACCOUNT_MATERIAL) && itemMeta.getDisplayName().equals(InventoryConstants.ADD_BANK_ACCOUNT_TEXT)){
            this.coinsystem.getPlayerService().addBankAccount((Player) event.getWhoClicked());
            showPlayerBankAccounts((Player) event.getWhoClicked());
        } else if(itemMaterial.equals(InventoryConstants.BANK_ACCOUNT_MATERIAL)){
            AccountEntity accountEntity = new Gson().fromJson(itemMeta.getPersistentDataContainer().get(new NamespacedKey(this.coinsystem,"playerBankData"),PersistentDataType.STRING),AccountEntity.class);
            InventoryUtils.showPlayerBankAccount((Player) event.getWhoClicked(), accountEntity, this.coinsystem);
        }
    }

    private void handleBankAccountInventory(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        Material itemMaterial = itemStack.getType();
        AccountEntity accountEntity = new Gson().fromJson(itemMeta.getPersistentDataContainer().get(new NamespacedKey(this.coinsystem,"playerBankData"),PersistentDataType.STRING),AccountEntity.class);
        Player player = (Player) event.getWhoClicked();

        if(itemMaterial.equals(InventoryConstants.DEPOSIT_COINS_MATERIAL)){

            InventoryUtils.createSignForAmountInput(this.coinsystem, accountEntity, player, true);
        } else if(itemMaterial.equals(InventoryConstants.WITHDRAW_COINS_MATERIAL)){
            InventoryUtils.createSignForAmountInput(this.coinsystem, accountEntity, player, false);
        } else if(itemMaterial.equals(InventoryConstants.TRANSACTION_MATERIAL)){
            event.setCancelled(true);
        } else if (itemMaterial.equals(InventoryConstants.RENAME_ACCOUNT_MATERIAL)){
            InventoryUtils.createSignForNameInput(this.coinsystem, accountEntity, player);
        } else if(itemMaterial.equals(InventoryConstants.DELETE_ACCOUNT_MATERIAL)){
            if(accountEntity.getBalance().equals(0L)){
                this.coinsystem.getPlayerService().deleteBankAccount(accountEntity, player.getUniqueId());
                showPlayerBankAccounts(player);
            } else{
                player.sendMessage(ChatColor.RED + "Account balance has to be 0 to delete");
            }
        }


    }
}
