package com.clawhub.registrycenter.core.bdb;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;

/**
 * <Description> BDB数据库环境,可以缓存StoredClassCatalog并共享 <br>
 *
 * @author 李志明<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月14日 <br>
 */
public class BdbEnvironment extends Environment {

    /**
     * The Class catalog.
     */
    private StoredClassCatalog classCatalog;

    /**
     * The Class catalog db.
     */
    private Database classCatalogDB;

    /**
     * Instantiates a new Bdb environment.
     *
     * @param envHome the env home
     * @param envConfig the env config
     */
    public BdbEnvironment(File envHome, EnvironmentConfig envConfig) {
        super(envHome, envConfig);
    }

    /**
     * Gets class catalog.
     *
     * @return the class catalog
     */
    public StoredClassCatalog getClassCatalog() {
        if (classCatalog == null) {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            try {
                classCatalogDB = openDatabase(null, "classCatalog", dbConfig);
                classCatalog = new StoredClassCatalog(classCatalogDB);
            } catch (DatabaseException e) {
                throw e;
            }
        }
        return classCatalog;
    }

    /**
     * Close.
     */
    @Override
    public synchronized void close() {
        if (classCatalogDB != null) {
            classCatalogDB.close();
        }
        super.close();
    }
}
