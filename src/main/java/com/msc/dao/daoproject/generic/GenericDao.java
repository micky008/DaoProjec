package com.msc.dao.daoproject.generic;

import com.msc.dao.daoproject.helper.SearchById;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe que doit etendre les future DAO. Les DAO peuvent uniquement utiliser
 * les les methode preparedXXX ou insert directement. SI un field ne doit pas
 * etre comptabilisé il faut mettre l'annotation
 * {@link util.dao.annotation.AutoGenerated} exemple dans Facture. Ce généric
 * DAO ainsi que certaine annotation sont prevu pour ORACLE uniquement.
 *
 * @author micky
 * @param <T> une classe venant du package util.dao.entity
 */
public interface GenericDao<T> {

    public List<T> getAll() throws SQLException;

    /**
     * Insert un objet.
     *
     * @param t
     * @throws SQLException
     */
    public void insert(T t) throws SQLException;

    public void insert(List<T> ts) throws SQLException;

    public void close();

    /**
     * Il faut faire return preparedUpdate();.
     *
     * @param t
     * @throws SQLException
     */
    public void update(T t) throws SQLException;

    public void update(List<T> t) throws SQLException;

    /**
     * Il faut faire return preparedDelete();.
     *
     * @param t
     * @throws SQLException
     */
    public void delete(T t) throws SQLException;

    public void delete(List<T> t) throws SQLException;
    
    public void deleteObjectById(Object... id) throws SQLException;

    public T getObjectById(Object... ids) throws SQLException;
    
    public List<T> getObjectsById(SearchById... ids) throws SQLException;

    public void truncate() throws SQLException;
    
    
}
