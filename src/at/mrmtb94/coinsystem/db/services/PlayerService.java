package at.mrmtb94.coinsystem.db.services;

import at.mrmtb94.coinsystem.Coinsystem;
import at.mrmtb94.coinsystem.db.entities.AccountEntity;
import at.mrmtb94.coinsystem.db.entities.PlayerEntity;
import at.mrmtb94.coinsystem.db.entities.TransactionEntity;
import at.mrmtb94.coinsystem.mc.constants.InventoryConstants;
import at.mrmtb94.coinsystem.mc.utils.InventoryUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class PlayerService {
    private MongoClient mongoClient;
    private Coinsystem coinsystem;


    public PlayerEntity getPlayerBank(UUID player){
        MongoDatabase database = mongoClient.getDatabase("coinsystem");
        MongoCollection<PlayerEntity> players = database.getCollection("playerBankAccounts", PlayerEntity.class);
        PlayerEntity playerEntity = players.find(Filters.eq("player",player.toString())).first();
        if(playerEntity == null){
            System.out.println("createPlayer");
            playerEntity = new PlayerEntity();
            playerEntity.setUuid(UUID.randomUUID().toString());
            playerEntity.setPlayer(player.toString());
            playerEntity.setAccounts(new ArrayList<>());
            players.insertOne(playerEntity);
        }
        return playerEntity;
    }

    public void addBankAccount(Player player){
        MongoDatabase database = mongoClient.getDatabase("coinsystem");
        MongoCollection<PlayerEntity> players = database.getCollection("playerBankAccounts", PlayerEntity.class);
        PlayerEntity playerEntity = players.find(Filters.eq("player",player.getUniqueId().toString())).first();
        if(playerEntity == null){
            System.out.println("createPlayer");
            playerEntity = new PlayerEntity();
            playerEntity.setUuid(UUID.randomUUID().toString());
            playerEntity.setPlayer(player.toString());
            playerEntity.setAccounts(new ArrayList<>());
            players.insertOne(playerEntity);
        }
        if(playerEntity.getAccounts().size() < InventoryConstants.MAX_ACCOUNT_SIZE) {
            AccountEntity accountEntity = new AccountEntity(UUID.randomUUID().toString(), "Account #" + (playerEntity.getAccounts().size() + 1), 0L, new ArrayList<>());
            playerEntity.getAccounts().add(accountEntity);
            players.findOneAndReplace(Filters.eq("player", player.getUniqueId().toString()), playerEntity);
        } else{
            player.sendMessage(ChatColor.RED + "Max amount of accounts reached");
        }

    }

    public AccountEntity renameBankAccount(Player player, AccountEntity accountEntity, String accountName){
        MongoDatabase database = mongoClient.getDatabase("coinsystem");
        MongoCollection<PlayerEntity> players = database.getCollection("playerBankAccounts", PlayerEntity.class);
        PlayerEntity playerEntity = players.find(Filters.eq("player", player.getUniqueId().toString())).first();

        ListIterator accountIterator = playerEntity.getAccounts().listIterator();
        AccountEntity account = accountEntity;
        while(accountIterator.hasNext()){
            account = (AccountEntity) accountIterator.next();

            if(account.getUuid().equals(accountEntity.getUuid())){
                account.setAccountName(accountName);
                accountIterator.set(account);
                break;
            }
        }
        players.findOneAndReplace(Filters.eq("player",player.getUniqueId().toString()), playerEntity);
        return account;
    }

    public void deleteBankAccount(AccountEntity accountEntity, UUID player){
        MongoDatabase database = mongoClient.getDatabase("coinsystem");
        MongoCollection<PlayerEntity> players = database.getCollection("playerBankAccounts", PlayerEntity.class);
        PlayerEntity playerEntity = players.find(Filters.eq("player", player.toString())).first();
        List<AccountEntity> accountEntityList = playerEntity.getAccounts().stream()
                .filter(account -> !account.getUuid().equals(accountEntity.getUuid()))
                .collect(Collectors.toList());
        playerEntity.setAccounts(accountEntityList);
        players.findOneAndReplace(Filters.eq("player",player.toString()), playerEntity);

    }

    public AccountEntity updateBankAccountBalance(AccountEntity accountEntity, Player player, Long amount, boolean isDeposit){
        MongoDatabase database = mongoClient.getDatabase("coinsystem");
        MongoCollection<PlayerEntity> players = database.getCollection("playerBankAccounts", PlayerEntity.class);
        PlayerEntity playerEntity = players.find(Filters.eq("player", player.getUniqueId().toString())).first();

        if(isDeposit) {
            int invAmount = 0;
            for (ItemStack itemStackInv : player.getInventory().getStorageContents()) {
                if (itemStackInv != null && itemStackInv.getType().equals(InventoryConstants.COINS_MATERIAL)) {
                    invAmount = invAmount + itemStackInv.getAmount();
                }
            }

            if (invAmount < amount.intValue()) {
                player.sendMessage(ChatColor.RED + "Transaction not possible - not enough Coins in Inventory");
                return accountEntity;
            }
        }

        if(!isDeposit && amount>0){
            amount = amount * -1L;
        }
        Long newBalance = accountEntity.getBalance() + amount;
        if(newBalance >= 0L) {
            accountEntity.setBalance(accountEntity.getBalance() + amount);
        } else {
            player.sendMessage(ChatColor.RED + "Transaction not possible - balance would be below 0");
            return accountEntity;
        }
        List<TransactionEntity> transactionEntities = accountEntity.getTransactions();
        transactionEntities.add(new TransactionEntity(UUID.randomUUID().toString(), amount, System.currentTimeMillis()));
        accountEntity.setTransactions(transactionEntities);


        ListIterator accountIterator = playerEntity.getAccounts().listIterator();
        while(accountIterator.hasNext()){
            AccountEntity account = (AccountEntity) accountIterator.next();
            if(account.getUuid().equals(accountEntity.getUuid())){
                accountIterator.set(accountEntity);
                break;
            }
        }
        players.findOneAndReplace(Filters.eq("player",player.getUniqueId().toString()), playerEntity);

        if(!isDeposit){
            amount = amount * -1L;
            player.getInventory().addItem(InventoryUtils.createNewItemStack(InventoryConstants.COINS_MATERIAL, amount.intValue(), coinsystem));
           player.sendMessage(ChatColor.GREEN + "Withdrew "+amount+" Coins");
        } else{
            player.getInventory().removeItem(InventoryUtils.createNewItemStack(InventoryConstants.COINS_MATERIAL, amount.intValue(), coinsystem));
            player.sendMessage(ChatColor.GREEN + "Deposited "+amount+" Coins");
        }

        return accountEntity;
    }
}
