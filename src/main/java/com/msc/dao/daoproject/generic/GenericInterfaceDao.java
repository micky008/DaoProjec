package com.msc.dao.daoproject.generic;

import java.sql.SQLException;

/**
 *
 * @author micky
 */
public interface GenericInterfaceDao<T> {

    /**
     * Retourne le dernier objet inseré.
     *
     * @return
     * @throws SQLException
     */
    T getLastInsert() throws SQLException;

    /**
     * Retourne le SQL pour une date.
     *
     * @param date la date a formatter
     * @param pattern le pattern de la date
     * @return TO_DATE('xx/yy/zzzz','dd/mm/yyyy');
     */
    String toDate(String date);

    String toDate(java.util.Date date);
}
