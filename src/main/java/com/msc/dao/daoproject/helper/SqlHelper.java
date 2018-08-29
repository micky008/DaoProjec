package com.msc.dao.daoproject.helper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author micky
 */
public class SqlHelper {

    /**
     *
     * @param liste
     * @return
     */
    public static String toIn(List liste) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Object o : liste) {
            sb.append(o);
            sb.append(',');
        }
        sb.trimToSize();
        sb = sb.delete(sb.length() - 1, sb.length());
        sb.append(')');
        return sb.toString();
    }

    public static String toIn(Object... liste) {
        return toIn(Arrays.asList(liste));
    }

    public class SearchById {

        private Field f;
        private Object o;

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

}
