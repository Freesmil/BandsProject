package cz.muni.fi.pv168.bandsproject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.rmi.server.LogStream.log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;


/**
* Spring Java configuration class. See http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java
*
* @author Tomas
*/

//import org.apache.derby.jdbc.ClientDriver
@Configuration  //je to konfigurace pro Spring
@EnableTransactionManagement //bude ?�dit transakce u metod ozna?en�ch @Transactional
public class SpringConfig {

    @Bean
    public DataSource dataSource(){

        try {
            Properties myconf = new Properties();
            myconf.load(MainGUI.class.getResourceAsStream("/conf.properties"));
            
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl(myconf.getProperty("jdbc.url"));
            ds.setUsername(myconf.getProperty("jdbc.user"));
            ds.setPassword(myconf.getProperty("jdbc.password"));
            try(Connection con = ds.getConnection()) {
                    for (String line : Files.readAllLines(Paths.get("src", "main", "resources", "band-schema.sql"))) {
                        if(line.trim().isEmpty()) continue;
                        if(line.endsWith(";")) line=line.substring(0,line.length()-1);
                        try (PreparedStatement st1 = con.prepareStatement(line)) {
                            st1.execute();
                        }
                    }
                    
                    for (String line : Files.readAllLines(Paths.get("src", "main", "resources", "fill-table.sql"))) {
                        if(line.trim().isEmpty()) continue;
                        if(line.endsWith(";")) line=line.substring(0,line.length()-1);
                        try (PreparedStatement st1 = con.prepareStatement(line)) {
                            st1.execute();
                        }
                    }
                } catch (Exception e) {
                    
                }
            return ds;
        } catch (IOException e) {
            
        }
        
        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .setName("bandDB")
                .addScript("classpath:band-schema.sql")
                .addScript("classpath:fill-table.sql")
                .build();

    }

    @Bean //pot?eba pro @EnableTransactionManagement
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean //n� manager, bude obalen ?�zen�m transakc�
    public CustomerManager customerManager() {
        return new CustomerManagerImpl(dataSource());
    }

    @Bean
    public BandManager bandManager() {
        return new BandManagerImpl(new TransactionAwareDataSourceProxy(dataSource()));
    }

    @Bean
    public LeaseManager leaseManager() {
        return new LeaseManagerImpl(dataSource());
    }
}
