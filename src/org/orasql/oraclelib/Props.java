package org.orasql.oraclelib;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class Props extends Properties {

    private static Props instance = null;

    private Props(){
        super();
        initDefaults();
    }

    public static Props getInstance(){
        if (instance == null) {
            instance = new Props();

        }
        return instance;
    }

    private void initDefaults(){
        try {
            InputStream is = Props.class.getResourceAsStream("default.oracle.properties");
            this.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String propKey){
        return Props.getInstance().getProperty(propKey,"");
    }

    public static String getValue(String propKey,String def){
        return Props.getInstance().getProperty(propKey,def);
    }

    public static int getIntValue(String propKey){
        return Integer.parseInt(Props.getInstance().getProperty(propKey, ""));
    }

    public static int getIntValue(String propKey, int def){
        return Integer.parseInt(Props.getInstance().getProperty(propKey, Integer.toString(def)));
    }

    public static void setValue(String propKey,String propValue){
        Props.getInstance().setProperty(propKey, propValue);
    }

    public static void setValue(String propKey,int propValue){
        Props.getInstance().setProperty(propKey, Integer.toString(propValue));
    }

    public static void save(String fileUrl) throws IOException {
        OutputStream f = new FileOutputStream( fileUrl );
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Props.getInstance().store(f, sdf.format(cal.getTime()));
    }

    public static void load(String fileUrl) throws IOException {
        FileInputStream fin = new FileInputStream(fileUrl);
        getInstance().instance.load(fin);
        fin.close();
    }
}