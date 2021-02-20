package at.mrmtb94.coinsystem.utils;


import at.mrmtb94.coinsystem.Coinsystem;
import at.mrmtb94.coinsystem.db.entities.AccountEntity;
import at.mrmtb94.coinsystem.db.entities.PlayerEntity;
import at.mrmtb94.coinsystem.db.entities.TransactionEntity;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.util.*;

public class InterestJob {

    private static double interestPercentage = 1.02;
    private Coinsystem coinSystem;
    private Timer timer = new Timer();

    public InterestJob(Coinsystem coinSystem) {
        this.coinSystem = coinSystem;
        scheduleJobs();
    }

    private void scheduleJobs() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                calculateInterest();
            }
        }, new Date(), 60000);
    }

    public void calculateInterest() {
        MongoDatabase database = this.coinSystem.getDbClient().getMongoClient().getDatabase("coinsystem");
        MongoCollection<PlayerEntity> players = database.getCollection("playerBankAccounts", PlayerEntity.class);
        for(PlayerEntity playerEntity : players.find()){

            ListIterator accountIterator = playerEntity.getAccounts().listIterator();
            while(accountIterator.hasNext()){
                AccountEntity account = (AccountEntity) accountIterator.next();
                if(account.getBalance().equals(0L)){
                    continue;
                }
                Long newBalance = Math.round(account.getBalance() * interestPercentage);
                Long difference = newBalance - account.getBalance();
                List<TransactionEntity> transactionEntityList = account.getTransactions();
                transactionEntityList.add(new TransactionEntity(UUID.randomUUID().toString(),difference, System.currentTimeMillis()));
                account.setTransactions(transactionEntityList);

                accountIterator.set(account);
            }
            players.findOneAndReplace(Filters.eq("uuid", playerEntity.getUuid()), playerEntity);
        }

    }
}
