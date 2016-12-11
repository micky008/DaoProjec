package com.msc.dao.daoproject.helper;

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
        sb = sb.delete(sb.length()-1, sb.length());
        sb.append(')');
        return sb.toString();
    }

    public static String toIn(Object... liste) {
        return toIn(Arrays.asList(liste));
    }
    
}
