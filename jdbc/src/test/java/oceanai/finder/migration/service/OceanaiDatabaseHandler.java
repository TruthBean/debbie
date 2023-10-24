package oceanai.finder.migration.service;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory;
import com.truthbean.debbie.jdbc.repository.DdlRepository;
import com.truthbean.debbie.jdbc.repository.RepositoryHandler;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/09 16:28.
 */
public class OceanaiDatabaseHandler implements ApplicationContextAware {

    private final DataSourceConfiguration configuration = new DataSourceConfiguration(true);
    private final DdlRepository ddlRepository;
    private final RepositoryHandler repositoryHandler;

    private final DataSourceFactory dataSourceFactory;

    public OceanaiDatabaseHandler() {
        configuration.setEnable(true);
        configuration.setAutoCommit(false);
        configuration.setDriverName(DataSourceDriverName.mysql);
        configuration.setUrl("jdbc:mysql://192.168.1.12:3306/?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        configuration.setUser("root");
        // configuration.setPassword("%Oceanai@2021%");
        configuration.setPassword("oceanai");

        ddlRepository = new DdlRepository();

        repositoryHandler = new RepositoryHandler();

        dataSourceFactory = new DefaultDataSourceFactory().factory(configuration);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {

    }

    public void loadSql() {
        try (TransactionInfo transactionInfo = dataSourceFactory.getTransaction()) {
            String path = "/Volumes/docker/workspace/truthbean/debbie/jdbc/src/test/resources/";
            InputStream stream = new FileInputStream(path + "finder_cloud.sql");
            String str = StreamHelper.streamToString(stream);
            repositoryHandler.execute(LOGGER, transactionInfo, str);

            stream = new FileInputStream(path + "finder_worker.sql");
            str = StreamHelper.streamToString(stream);
            String[] split = str.split(";");
            for (String sql : split) {
                repositoryHandler.execute(LOGGER, transactionInfo, sql);
            }
            transactionInfo.commit();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(OceanaiDatabaseHandler.class);
}
