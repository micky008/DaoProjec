package com.msc.dao.daoproject.helper;

import java.util.Properties;

/**
 *
 * @author Michael
 */
public class DAOConfig {

    public enum BDD_SUPPORTED {
        MYSQL, SQLLITE;
    }

    private String driver;
    private String url;
    private String login;
    private String password;
    private BDD_SUPPORTED type;

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the type
     */
    public BDD_SUPPORTED getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(BDD_SUPPORTED type) {
        this.type = type;
    }
    
    public static DAOConfig getConfig(Properties prop) {
        DAOConfig conf = new DAOConfig();
        conf.setDriver(prop.getProperty("bdd.driver"));
        conf.setLogin(prop.getProperty("bdd.login"));
        conf.setPassword(prop.getProperty("bdd.password"));
        conf.setUrl(prop.getProperty("bdd.url"));
        conf.setType(BDD_SUPPORTED.valueOf(prop.getProperty("bdd.type").toUpperCase()));        
        return conf;
    }



}
