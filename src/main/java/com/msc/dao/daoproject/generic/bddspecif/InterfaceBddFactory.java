package com.msc.dao.daoproject.generic.bddspecif;

import com.msc.dao.daoproject.generic.DAO;
import com.msc.dao.daoproject.generic.GenericDao;
import com.msc.dao.daoproject.generic.GenericInterfaceDao;

/**
 *
 * @author micky
 */
public class InterfaceBddFactory {

    private static MySqlDAO mysqldao;
    private static SqliteDAO sqlitedao;

    public static GenericInterfaceDao getInterface(GenericDao gen) {
        switch (DAO.getBddImpl()) {
            case MYSQL:
                if (mysqldao == null) {
                    mysqldao = new MySqlDAO(gen);
                }
                return mysqldao;
            case SQLLITE:
                if (sqlitedao == null) {
                    sqlitedao = new SqliteDAO(gen);
                }
                return sqlitedao;
            default:
                return new SqliteDAO(gen);
        }
    }

}
