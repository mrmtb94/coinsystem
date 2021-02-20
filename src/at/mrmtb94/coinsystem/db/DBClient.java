package at.mrmtb94.coinsystem.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

@Getter
public class DBClient{

    private DBClient dbClient;
    private MongoClient mongoClient;
    private static final String MONGO_CONNECTION_STRING = "mongodb://localhost";



    public DBClient(){
        initializeMongoDb();
    }

    private void initializeMongoDb(){
        ConnectionString connectionString = new ConnectionString(MONGO_CONNECTION_STRING);
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        this.mongoClient = MongoClients.create(clientSettings);
    }
}
