package at.mrmtb94.coinsystem.mc.utils;

import at.mrmtb94.coinsystem.Coinsystem;
import at.mrmtb94.coinsystem.db.entities.AccountEntity;
import at.mrmtb94.coinsystem.db.entities.TransactionEntity;
import at.mrmtb94.coinsystem.mc.constants.InventoryConstants;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class InventoryUtils {


    public static List<String> getTransactionsAsList(AccountEntity accountEntity) {
        List<String> transactionList = new ArrayList<>();
        List<TransactionEntity> transactionEntities;
        if(accountEntity.getTransactions().size()>10){
            transactionEntities = accountEntity
                    .getTransactions()
                    .subList(accountEntity.getTransactions().size()-10, accountEntity.getTransactions().size());
        } else {
            transactionEntities = accountEntity.getTransactions();
        }
        transactionEntities.forEach(transactionEntity -> {
            ChatColor chatColor = transactionEntity.getAmount() > 0L ? ChatColor.GREEN : ChatColor.RED;
            transactionList.add(chatColor + InventoryConstants.sdf.format(new Date(transactionEntity.getTime())) + " | Amount: " + transactionEntity.getAmount());
        });
        return transactionList;
    }

    public static ItemStack createNewItemStack(Material material, int amount, Plugin plugin){
        return createNewItemStack(material, amount, null, null, null, plugin);
    }
    public static ItemStack createNewItemStack(Material material, int amount, String displayname, Plugin plugin){
        return createNewItemStack(material, amount, displayname, null, null, plugin);
    }
    public static ItemStack createNewItemStack(Material material, int amount, String displayname, List<String> lore, Plugin plugin){
        return createNewItemStack(material, amount, displayname, lore, null, plugin);
    }
    public static ItemStack createNewItemStack(Material material, int amount, String displayname, Map<String, Object> itemPersistentData, Plugin plugin){
        return createNewItemStack(material, amount, displayname, null, itemPersistentData, plugin);
    }
    public static ItemStack createNewItemStack(Material material, int amount, String displayname, List<String> lore, Map<String, Object> itemPersistentData, Plugin plugin) {
        Gson gson = new Gson();
        ItemStack itemStack = new ItemStack(material,amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(StringUtils.isNotEmpty(displayname)) {
            itemMeta.setDisplayName(displayname);
        }

        if(itemPersistentData != null && itemPersistentData.size() > 0){
            itemPersistentData.forEach((key, value) -> {
                itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, gson.toJson(value));
            });
        }
        if(lore != null && !lore.isEmpty()){
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void createSignForAmountInput(Coinsystem plugin, AccountEntity accountEntity, Player player, boolean isDeposit) {
        plugin.getSignMenuFactory()
                .newMenu(Lists.newArrayList("", "^^^^^^^^^^", "Please enter", "amount"))
                .response((playerSign, lines) -> {
                    try{
                        Long amount = Long.parseLong(lines[0]);
                        AccountEntity accountEntity1 = plugin.getPlayerService().updateBankAccountBalance(accountEntity, player, amount, isDeposit);
                        showPlayerBankAccount(player,accountEntity1, plugin);
                        return true;
                    } catch(Exception e){
                        return false;
                    }

                })
                .open(player);
    }
    public static void createSignForNameInput(Coinsystem plugin, AccountEntity accountEntity, Player player) {
        plugin.getSignMenuFactory()
                .newMenu(Lists.newArrayList("", "^^^^^^^^^^", "Please enter", "new name"))
                .response((playerSign, lines) -> {
                    try{
                        if(StringUtils.isNotEmpty(lines[0])) {

                            showPlayerBankAccount(player, plugin.getPlayerService().renameBankAccount(player, accountEntity, lines[0]), plugin);
                            player.sendMessage(ChatColor.GREEN + "Renamed Account");
                        } else{
                            player.sendMessage(ChatColor.RED + "Could not rename Account");
                        }
                        return true;
                    } catch(Exception e){
                        return false;
                    }

                })
                .open(player);
    }

    public static void showPlayerBankAccount(Player player, AccountEntity accountEntity, Coinsystem coinsystem){
        Inventory inventory = Bukkit.createInventory(player, InventoryConstants.INVENTORY_SIZE, InventoryConstants.BANK_ACCOUNT_INV);

        inventory.setItem(4,InventoryUtils.createNewItemStack(InventoryConstants.BANK_ACCOUNT_MATERIAL,
                InventoryConstants.INVENTORY_ACTIONS_SIZE,
                accountEntity.getAccountName(),
                Arrays.asList("Balance: "+accountEntity.getBalance(),"Transactions: "+accountEntity.getTransactions().size()),
                Map.of("playerBankData", accountEntity),
                coinsystem));

        inventory.setItem(InventoryConstants.DEPOSIT_COINS_POSITION,InventoryUtils.createNewItemStack(InventoryConstants.DEPOSIT_COINS_MATERIAL,
                InventoryConstants.INVENTORY_ACTIONS_SIZE,
                InventoryConstants.DEPOSIT_COINS_TEXT,
                Map.of("playerBankData", accountEntity),
                coinsystem));

        inventory.setItem(InventoryConstants.WITHDRAW_COINS_POSITION,InventoryUtils.createNewItemStack(InventoryConstants.WITHDRAW_COINS_MATERIAL,
                InventoryConstants.INVENTORY_ACTIONS_SIZE,
                InventoryConstants.WITHDRAW_COINS_TEXT,
                Map.of("playerBankData", accountEntity),
                coinsystem));

        inventory.setItem(InventoryConstants.TRANSACTION_POSITION,InventoryUtils.createNewItemStack(InventoryConstants.TRANSACTION_MATERIAL,
                InventoryConstants.INVENTORY_ACTIONS_SIZE,
                InventoryConstants.TRANSACTION_TEXT,
                InventoryUtils.getTransactionsAsList(accountEntity),
                Map.of("playerBankData", accountEntity),
                coinsystem));

        inventory.setItem(InventoryConstants.INVENTORY_SIZE-InventoryConstants.RENAME_ACCOUNT_MAX_POSITION,InventoryUtils.createNewItemStack(InventoryConstants.RENAME_ACCOUNT_MATERIAL,
                InventoryConstants.INVENTORY_ACTIONS_SIZE,
                InventoryConstants.RENAME_ACCOUNT_TEXT,
                Map.of("playerBankData", accountEntity),
                coinsystem));

        inventory.setItem(InventoryConstants.INVENTORY_SIZE-InventoryConstants.DELETE_ACCOUNT_MAX_POSITION,InventoryUtils.createNewItemStack(InventoryConstants.DELETE_ACCOUNT_MATERIAL,
                InventoryConstants.INVENTORY_ACTIONS_SIZE,
                InventoryConstants.DELETE_ACCOUNT_TEXT,
                Map.of("playerBankData", accountEntity),
                coinsystem));

        player.openInventory(inventory);
    }
}
