package com.msc.dao.daoproject.generic;

import com.msc.dao.daoproject.helper.DAOConfig;
import com.msc.dao.daoproject.helper.DAOConfig.BDD_SUPPORTED;
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

    private static BDD_SUPPORTED bddEmploye;

    private static Connection con;
    protected static DAOConfig config;

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
            config = DAOConfig.getConfig(prop);
            try {               
                Class.forName(config.getDriver());
                con = DriverManager.getConnection(config.getUrl(), config.getLogin(), config.getPassword());
                con.setAutoCommit(false);
                bddEmploye = config.getType();
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
