package com.msc.dao.daoproject.helper;

import java.lang.reflect.Field;

/**
 *
 * @author Michael
 */
public class SearchById {

    private Field f;
    private Object o;

    public SearchById() {
    }

    public SearchById(Field f, Object o) {
        this.f = f;
        this.o = o;
    }

    /**
     * @return the f
     */
    public Field getF() {
        return f;
    }

    /**
     * @param f the f to set
     */
    public void setF(Field f) {
        this.f = f;
    }

    /**
     * @return the o
     */
    public Object getO() {
        return o;
    }

    /**
     * @param o the o to set
     */
    public void setO(Object o) {
        this.o = o;
    }

}
