package com.msc.dao.daoproject.generic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Micky
 */
public class DAO {

    public enum BDD_SUPPORTED {
        MYSQL, SQLLITE;
    }
      private static BDD_SUPPORTED bddEmploye;

    private static Connection con;
    protected static Properties config;

    private static Properties pIitConnection;

    /**
     * Initialise la connection avec la BDD. c'est la 1er methode a faire apres
     * le main [et le fichier de config].
     *
     * @param prop Doit avoir comme clé:
     * <ul>
     * <li>bdd.driver</li>
     * <li>bdd.url</li>
     * <li>bdd.login</li>
     * <li>bdd.password</li>
     * <li>bdd.type</li>
     * </ul>
     */
    public static void initConnection(Properties prop) {
        initConnection(prop, false);
    }

    /**
     * Initialise la connection avec la BDD. c'est la 1er methode a faire apres
     * le main [et le fichier de config].
     *
     * @param prop Doit avoir comme clé:
     * <ul>
     * <li>bdd.driver</li>
     * <li>bdd.url</li>
     * <li>bdd.login</li>
     * <li>bdd.password</li>
     * <li>bdd.type</li>
     * </ul>
     * @param force doit toujours etre false. sauf si on veux se reconnecter
     */
    public static void initConnection(Properties prop, boolean force) {
        if (prop == null) {
            prop = new Properties();
        }
        if (con == null || force) {
            config = prop;
            try {
                String jdbcDriver = prop.getProperty("bdd.driver", "org.postgresql.Driver");
                String jdbcUrl = prop.getProperty("bdd.url");
                String loginBdd = prop.getProperty("bdd.login");
                String passwordBdd = prop.getProperty("bdd.password");
                Class.forName(jdbcDriver);
                con = DriverManager.getConnection(jdbcUrl, loginBdd, passwordBdd);
                con.setAutoCommit(false);
                bddEmploye = BDD_SUPPORTED.valueOf(prop.getProperty("bdd.type").toUpperCase());
                pIitConnection = prop;
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Connection getConnection() {
        return con;
    }

    public static void closeConnection() {
        try {
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(DAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDebugMode(boolean debugMode) {
        GenericDaoImpl.DEBUG_MODE = debugMode;
    }

    public static boolean getDebugMode() {
        return GenericDaoImpl.DEBUG_MODE;
    }

    public static BDD_SUPPORTED getBddImpl() {
        return bddEmploye;
    }

    public static void refreshConnection() {
        initConnection(pIitConnection, true);
        if (getDebugMode()) {
            System.out.println("Init DAO Connexion OK");
        }
    }


}
