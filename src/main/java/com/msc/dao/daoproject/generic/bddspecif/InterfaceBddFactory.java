package com.msc.dao.daoproject.generic.bddspecif;

import com.msc.dao.daoproject.generic.DAO;
import com.msc.dao.daoproject.generic.GenericDao;
import com.msc.dao.daoproject.generic.GenericInterfaceDao;

/**
 *
 * @author micky
 */
public class InterfaceBddFactory {


    public static GenericInterfaceDao getInterface(GenericDao gen) {
        switch (DAO.getBddImpl()) {
            case MYSQL:
                return new MySqlDAO(gen);
            case SQLLITE:
                return new SqliteDAO(gen);
            default:
                return new SqliteDAO(gen);
        }
    }

}
