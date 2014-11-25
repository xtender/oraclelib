package org.orasql.oraclelib;

import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;


@SuppressWarnings("unused")
public class DBInstance {
    private static final Logger logger = LoggerFactory.getLogger(DBInstance.class);

    private String host;
    private int    port;
    private String serviceName;
    private String username;
    private String userpass;

    private PoolDataSource pds;
    private transient OracleConnection connection;

    private static final int POOL_INITIAL_SIZE = 1;
    private static final int POOL_MIN_SIZE = 1;
    private static final int POOL_MAX_SIZE = 10;

    public DBInstance(String host, int port, String serviceName, String username, String userpass) {
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
        this.username = username;
        this.userpass = userpass;
    }

    private void initPool() throws SQLException {
        if(pds==null) {
            pds = PoolDataSourceFactory.getPoolDataSource();
            //Setting connection properties of the data source
            pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
            pds.setURL(this.getURL());
            pds.setUser(username);
            pds.setPassword(userpass);
            //Setting pool properties
            pds.setInitialPoolSize(POOL_INITIAL_SIZE);
            pds.setMinPoolSize(POOL_MIN_SIZE);
            pds.setMaxPoolSize(POOL_MAX_SIZE);
        }
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

    public oracle.jdbc.OracleConnection getPoolConnection() throws SQLException {
        initPool();
        return (OracleConnection) pds.getConnection();
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
