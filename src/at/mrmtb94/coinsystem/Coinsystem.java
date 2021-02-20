package at.mrmtb94.coinsystem;

import at.mrmtb94.coinsystem.db.DBClient;
import at.mrmtb94.coinsystem.db.entities.PlayerEntity;
import at.mrmtb94.coinsystem.db.services.PlayerService;
import at.mrmtb94.coinsystem.mc.events.InventoryClickEventHandler;
import at.mrmtb94.coinsystem.mc.events.PlayerJoinEventHandler;
import at.mrmtb94.coinsystem.mc.utils.SignMenuFactory;
import at.mrmtb94.coinsystem.utils.InterestJob;
import com.mongodb.DB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Coinsystem extends JavaPlugin {

    private DBClient dbClient;
    private InventoryClickEventHandler inventoryClickEventHandler;
    private PlayerService playerService;
    private SignMenuFactory signMenuFactory;
    private InterestJob interestJob;

    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        dbClient = new DBClient();
        initializeServices();
        initializeEvents();
        this.signMenuFactory = new SignMenuFactory(this);
        this.interestJob = new InterestJob(this);
        super.onEnable();
    }

    private void initializeServices(){
        this.playerService = new PlayerService(this.dbClient.getMongoClient(), this);
    }

    private void initializeEvents(){
        this.inventoryClickEventHandler = new InventoryClickEventHandler(this);
        getServer().getPluginManager().registerEvents(this.inventoryClickEventHandler, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(),this);
    }


}
