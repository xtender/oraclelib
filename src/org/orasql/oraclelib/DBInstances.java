package org.orasql.oraclelib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class DBInstances {
    private static final Logger logger = LoggerFactory.getLogger(DBInstances.class);

    private static DBInstances instance;
    private Map<String,DBInstance> dbInstanceMap = new HashMap<>();

    private DBInstances() {
    }

    private static DBInstances getInstance(){
        if(instance == null) {
            instance = new DBInstances();
        }
        return instance;
    }

    public static DBInstance getDB(String dbName) {

        if(getInstance().dbInstanceMap == null) {
            return null;
        }else{
            if (getInstance().dbInstanceMap.containsKey(dbName)) {
                return getInstance().dbInstanceMap.get(dbName);
            }else {
                for(String key:getInstance().dbInstanceMap.keySet()){
                    if(key.toLowerCase().equals(dbName.toLowerCase()))
                        return getInstance().dbInstanceMap.get(key);
                }
            }
        }
        return null;
    }

    public static void addDB(String dbName, DBInstance dbInstance){
        if(getInstance().dbInstanceMap == null) {
            getInstance().dbInstanceMap = Collections.singletonMap(dbName, dbInstance);
        }else{
            getInstance().dbInstanceMap.put(dbName,dbInstance);
        }

    }

    public static void loadFromJson(String file){

        Gson gson = new Gson();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            Type tInstantType = new TypeToken<HashMap<String,DBInstance>>(){}.getType();
            getInstance().dbInstanceMap = gson.fromJson(br,tInstantType);
            logger.debug("{}: loaded {} records from {}", DBInstances.class, getInstance().dbInstanceMap.size(), file);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
            getInstance().dbInstanceMap = new HashMap<>();
        }
    }

    public static void saveToJson(String file){
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(getInstance().dbInstanceMap);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
            logger.debug("saved to json({})",file);
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public static Set<String> getNames(){
        return getInstance().dbInstanceMap.keySet();
    }
}
