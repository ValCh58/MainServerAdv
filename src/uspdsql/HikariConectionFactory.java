package uspdsql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import mainserver.ConfigServer;

public class HikariConectionFactory {

    public final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HikariConectionFactory.class);
    private static final Properties prop = ConfigServer.getInstance();
    private static HikariDataSource hikariDataSource = null;
    private static final HikariConfig config = new HikariConfig();
    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private static final int SSL_ON = 1; 
    private static String sslSelect = null;
        
    private static void setConfig() {
        
        //config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setDriverClassName(prop.getProperty("DriverClassName"));
        //jdbc:mariadb://5.181.255.180:3306/eisystems
        //config.setJdbcUrl("jdbc:mysql://"    //Рабочий вариант!
        //        + prop.getProperty("ipHostMySql") + ":" + prop.getProperty("portMySql") + "/" + prop.getProperty("dbName")
        //        + "?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false");
        int ssl = Integer.parseInt(prop.getProperty("modeSSL"));
        sslSelect = ssl == SSL_ON ? "REQUIRED" : "DISABLED";
        config.setJdbcUrl("jdbc:mysql://" 
                + prop.getProperty("ipHostMySql") + ":" + prop.getProperty("portMySql") + "/" + prop.getProperty("dbName")
                + "?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&sslMode="+sslSelect);
        
        /*
        config.setJdbcUrl("jdbc:mariadb://" 
                + prop.getProperty("ipHostMySql") + ":" + prop.getProperty("portMySql") + "/" + prop.getProperty("dbName"));
        */        
        
        config.setUsername(prop.getProperty("dbUser"));
        config.setPassword(prop.getProperty("dbPwd")); //123456
        config.setMaximumPoolSize((CORE_SIZE * 2) + 1);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", prop.getProperty("cachePrepStmts"));
        config.addDataSourceProperty("prepStmtCacheSize", prop.getProperty("prepStmtCacheSize"));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", prop.getProperty("prepStmtCacheSqlLimit"));
        config.addDataSourceProperty("useServerPrepStmts", prop.getProperty("useServerPrepStmts"));
        config.addDataSourceProperty("useLocalSessionState", prop.getProperty("useLocalSessionState"));
        config.addDataSourceProperty("rewriteBatchedStatements", prop.getProperty("rewriteBatchedStatements"));
        config.addDataSourceProperty("cacheResultSetMetadata", prop.getProperty("cacheResultSetMetadata"));
        config.addDataSourceProperty("cacheServerConfiguration", prop.getProperty("cacheServerConfiguration"));
        config.addDataSourceProperty("elideSetAutoCommits", prop.getProperty("elideSetAutoCommits"));
        config.addDataSourceProperty("maintainTimeStats", prop.getProperty("maintainTimeStats"));
        //---------Configuration SSL-------------------------------------------------------------//
        if(ssl == SSL_ON){
           config.addDataSourceProperty("clientCertificateKeyStoreUrl", prop.getProperty("clientCertificateKeyStoreUrl"));//Path to store certificate//
           config.addDataSourceProperty("clientCertificateKeyStorePassword", prop.getProperty("clientCertificateKeyStorePassword"));
        }
        //config.addDataSourceProperty("clientCertificateKeyStoreUrl", "file:./config/truststore");        
        //config.addDataSourceProperty("clientCertificateKeyStorePassword", "123456");

    }

    private HikariConectionFactory() {
    }

    public static synchronized Connection getConnection() {
        if (hikariDataSource == null) {
            setConfig();
            hikariDataSource = new HikariDataSource(config);
        }
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }
}
