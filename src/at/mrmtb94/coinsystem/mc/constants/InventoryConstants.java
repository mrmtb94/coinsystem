package at.mrmtb94.coinsystem.mc.constants;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;

public class InventoryConstants {

    public static final Material COINS_MATERIAL = Material.GOLD_NUGGET;


    public static final String OPEN_BANK = "Open Bank";

    public static final String BANK_ACCOUNTS_INV = "Bank Accounts";
    public static final String BANK_ACCOUNT_INV = "Bank Account";
    public static final int INVENTORY_SIZE = 27;
    public static final int MAX_ACCOUNT_SIZE = 18;
    public static final int INVENTORY_ACTIONS_SIZE = 1;
    public static final Material BANK_ACCOUNT_MATERIAL = Material.BOOK;
    public static final int ADD_BANK_ACCOUNT_POSITION = 19;
    public static final Material ADD_BANK_ACCOUNT_MATERIAL = Material.EMERALD_BLOCK;
    public static final String ADD_BANK_ACCOUNT_TEXT = "Add Bank Account";


    public static final Material DEPOSIT_COINS_MATERIAL = Material.SHULKER_BOX;
    public static final Material WITHDRAW_COINS_MATERIAL = Material.DROPPER;
    public static final Material TRANSACTION_MATERIAL = Material.BOOKSHELF;
    public static final Material DELETE_ACCOUNT_MATERIAL = Material.LAVA_BUCKET;
    public static final Material RENAME_ACCOUNT_MATERIAL = Material.SPRUCE_SIGN;
    public static final int DEPOSIT_COINS_POSITION = 9;
    public static final int WITHDRAW_COINS_POSITION = 13;
    public static final int TRANSACTION_POSITION = 17;
    public static final int DELETE_ACCOUNT_MAX_POSITION = 1;
    public static final int RENAME_ACCOUNT_MAX_POSITION = 2;
    public static final String DEPOSIT_COINS_TEXT = "Deposit Coins";
    public static final String WITHDRAW_COINS_TEXT = "Withdraw Coins";
    public static final String TRANSACTION_TEXT = "Transactions";
    public static final String DELETE_ACCOUNT_TEXT = "Delete Account";
    public static final String RENAME_ACCOUNT_TEXT = "Rename Account";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

}
