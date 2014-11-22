package org.orasql.oraclelib;

import oracle.jdbc.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBInstance {
    private static final Logger logger = LoggerFactory.getLogger(DBInstance.class);

    private String host;
    private int    port;
    private String serviceName;
    private String username;
    private String userpass;
    private transient OracleConnection connection;

    public DBInstance(String host, int port, String serviceName, String username, String userpass) {
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.username = username;
        this.userpass = userpass;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getURL(){
        return "jdbc:oracle:thin:@//" + host + ":" + Integer.toString(port) + "/" + serviceName;
    }

    public oracle.jdbc.OracleConnection getConnection() throws SQLException {
        if(this.connection == null){
            this.connection = (OracleConnection) DriverManager.getConnection(this.getURL(),this.username, this.userpass);
        }
        return this.connection;
    }

    @Override
    public String toString() {
        return "DBInstance{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", serviceName='" + serviceName + '\'' +
                ", username='" + username + '\'' +
                ", userpass='" + userpass + '\'' +
                '}';
    }
}
