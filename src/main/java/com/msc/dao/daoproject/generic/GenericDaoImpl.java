package com.msc.dao.daoproject.generic;

import com.msc.dao.daoproject.annotation.ForceNull;
import com.msc.dao.daoproject.annotation.Id;
import com.msc.dao.daoproject.annotation.Name;
import com.msc.dao.daoproject.annotation.StaticField;
import com.msc.dao.daoproject.generic.bddspecif.InterfaceBddFactory;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;

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
public abstract class GenericDaoImpl<T> implements GenericDao<T> {

    /**
     * Si true, permet de voir toutes les requetes qui se forme avant execution.
     */
    public static boolean DEBUG_MODE = false;

    protected Connection con;
    protected Class<?> clazz;
    protected Statement st;
    protected LinkedList<Object> secureList = new LinkedList<>();

    // histoire de pouvoir faire super(con) lol...
    public GenericDaoImpl(Connection con) {
        // clazz =
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        String cl = pt.getActualTypeArguments()[0].toString().split("\\s")[1];
        try {
            @SuppressWarnings("unchecked")
            T t = (T) Class.forName(cl).newInstance();
            clazz = t.getClass();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated catch block
        // TODO Auto-generated catch block
        this.con = con;
    }

    /**
     * Convertie un Field en colonne sql.
     *
     * @param fieldName
     * @return
     */
    public String convertFieldToChamp(String fieldName) {
        StringBuilder res = new StringBuilder(fieldName.length() + 2);
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c)) {
                res.append('_');
            }
            res.append(c);
        }
        return res.toString().toLowerCase();
    }

    /**
     * Convertie une colonne en Field [camelCase]
     *
     * @param columnName
     * @return
     */
    public String convertChampToField(String columnName) {
        StringBuilder res = new StringBuilder(columnName.length() + 2);
        for (int i = 0; i < columnName.length(); i++) {
            char c = columnName.charAt(i);
            c = Character.toLowerCase(c);
            if (c == '_') {
                ++i;
                c = columnName.charAt(i);
                c = Character.toUpperCase(c);
            }
            res.append(c);
        }
        return res.toString();
    }

    /**
     * Cree la chaine "select * from ".
     *
     * @param prefix : permet de mettre un prefix au champ. ex: si prefix = "c"
     * alors le resultat serra: select c.champ1, c.champ2, c. etc... from"
     * @return select champ1, champ2, etc... from"
     */
    public String getFieldsForSelect(String prefix) {
        String res = "select " + convertFiledsToString(prefix) + " from ";
        return res;
    }

    /**
     * Permet de crée un String de type [champ1,champ2,champ3,etc...]
     *
     * @param prefix
     * @return
     */
    public String convertFiledsToString(String prefix) {
        StringBuilder sb = new StringBuilder();
        Field fields[] = getFields();
        for (Field field : fields) {
            if (field.getAnnotation(StaticField.class) != null) {
                continue;
            }
            sb.append(getColoumnName(field));
            sb.append(",");
        }
        sb = sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * Methode retournant le nom d'une table. Methode a override dans les DAO si
     * le nom de la table n'est pas correct.
     *
     * @return
     */
    public String getTableName() {
        return clazz.getAnnotation(Name.class).name();
    }

    /**
     * Methode a utiliser quand on veux faire un select dans le DAO. retournera
     * une liste instance d'objet sinon une liste vide.
     *
     * @param where ajouter le " where ..."
     * @param prefix Si dans le where on a plusieur jointure, ca permet de
     * localisé l'objet a retourné.
     * @return Une list d'objet ou une liste vide.
     * @throws SQLException
     */
    public List<T> preparedSelectMulti(String where, String prefix) throws SQLException {
        st = con.createStatement();
        String query = getFieldsForSelect(prefix) + getTableName() + " " + where + ";";
        if (DEBUG_MODE) {
            System.out.println(query);
        }
        ResultSet rs = st.executeQuery(query);
        return fillObjects(rs);
    }

    public List<T> preparedSelectMulti(String where) throws SQLException {
        return preparedSelectMulti(where, null);
    }

    /**
     * Methode a utiliser quand on veux faire un select dans le DAO. retournera
     * qu'un instance d'un objet sinon null.
     *
     * @param where
     * @param prefix
     * @return
     * @throws SQLException
     */
    public T preparedSelectOnce(String where, String prefix) throws SQLException {
        st = con.createStatement();
        String query = getFieldsForSelect(prefix) + getTableName() + " " + where + ";";
        if (DEBUG_MODE) {
            System.out.println(query);
        }
        ResultSet rs = st.executeQuery(query);
        return fillObject(rs);
    }

    public T preparedSelectOnce(String where) throws SQLException {
        return preparedSelectOnce(where, null);
    }

    public List<T> getAll() throws SQLException {
        return preparedSelectMulti("");
    }

    /**
     * Envois directement le SQL. pensez a fermer le statement [close()] et le
     * resultset ! a eviter tant que possible.
     *
     * @param query select * from truc
     * @return un resultset.
     * @throws SQLException
     */
    @Deprecated
    public ResultSet sendSql(String query) throws SQLException {
        st = con.createStatement();
        if (DEBUG_MODE) {
            System.out.println(query);
        }
        return st.executeQuery(query);

    }

    /**
     * Envois directement le SQL. pensez a fermer le resultset ! a eviter tant
     * que possible. Securisé car il faut remplir la List secureList dans l
     * 'ordre
     *
     * @param query select * from truc
     * @return un resultset.
     * @throws SQLException
     */
    @Deprecated
    public ResultSet sendSqlSecured(String query) throws SQLException {
        PreparedStatement ps = con.prepareStatement(query);
        int i = 0;
        for (Object key : secureList) {
            ps.setObject(i++, key);
        }
        if (DEBUG_MODE) {
            String query2 = query;
            for (Object key : secureList) {
                query2 = query2.replace("?", key.toString());
            }
            System.out.println(query2);
        }
        secureList.clear();
        return ps.executeQuery(query);
    }

    /**
     * Envois directement le SQL.
     *
     * @param query update/delete/autre
     * @return un resultset.
     * @throws SQLException
     */
    public Integer sendSqlUpdate(String fullQuery) throws SQLException {
        st = con.createStatement();
        if (DEBUG_MODE) {
            System.out.println(fullQuery);
        }
        int res = st.executeUpdate(fullQuery);
        con.commit();
        st.close();
        return res;

    }

    /**
     * Meme chose que fillObjects mais on retourne qu'un objet.
     *
     * @param rs
     * @return un objet sinon NULL.
     * @throws SQLException
     */
    public T fillObject(ResultSet rs) throws SQLException {
        List<T> l = fillObjects(rs);
        if (l != null && !l.isEmpty()) {
            T t = l.get(0);
            return t;
        }
        return null;
    }

    /**
     * Convertie un Resulset en une List&lg;T&gt;.
     *
     * @param rs
     * @return Retourne une liste vide si pas de resultat.
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public List<T> fillObjects(ResultSet rs) throws SQLException {
        List<T> objs = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        List<String> fieldNames = new ArrayList<>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            fieldNames.add(convertChampToField(rsmd.getColumnName(i)));
        }
        T obj;
        Object resTmp;
        while (rs.next()) {
            try {
                obj = (T) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                break;
            }
            int i = 1;
            for (String fieldName : fieldNames) {
                Field f = FieldUtils.getDeclaredField(clazz, fieldName, true);
                if (f.getAnnotation(ForceNull.class) != null) {
                    continue;
                }
                f.setAccessible(true);
                try {
                    Object oj = rs.getObject(i);
                    if (oj == null) {
                        i++;
                        continue;

                    }
                    if (f.getType() == BigInteger.class) {
                        int pre = rsmd.getScale(i);
                        if (pre == 0) {
                            if (oj.getClass() == Long.class) {
                                Long bd = (Long) oj;
                                oj = new BigInteger("" + bd);
                            }
                        }
                    }
                    if (f.getType() == Double.class) {
                        if (oj instanceof Integer) {
                            oj = new Double(1.0 * (Integer) oj);
                        } else {
                            BigDecimal bd = (BigDecimal) oj;
                            oj = new Double(bd.doubleValue());
                        }
                    }
                    resTmp = convertFillObjectCustom(f.getType(), oj);
                    if (resTmp != null) {
                        oj = resTmp;
                    }
                    f.set(obj, oj);
                } catch (IllegalArgumentException e) {
                    break;
                } catch (IllegalAccessException e) {
                    break;
                } catch (SQLException e) {
                    System.out.println(f.getName());
                    e.printStackTrace();
                }
                i++;
            }
            objs.add(obj);
        }
        rs.close();
        return objs;
    }

    /**
     * Convertie ce qui viens de la BDD en field.
     *
     * @param clazz le type venant de la classe [field]
     * @param res ce qui vient de la BDD
     * @return null ce n'est pas un type custom
     */
    protected abstract Object convertFillObjectCustom(Class<?> clazz, Object res);

    /**
     * Permet de faire un update localisé.
     *
     * @param t
     * @param where
     * @throws SQLException
     */
    public void preparedUpdate(T t, String where) throws SQLException {
        String sql = makeUpdate(t, where);
        st = con.createStatement();
        if (DEBUG_MODE) {
            System.out.println(sql);
        }
        st.executeUpdate(sql);
        con.commit();
        st.close();
    }

    /**
     * Convertie le Field en String mais formaté.
     * <ul>
     * <li>Si String => 'value'</li>
     * <li>Si Date => 'dd/MM/yyyy'</li>
     * <li>Autre => value</li>
     * </ul>
     *
     * @param f
     * @param t
     * @return
     */
    public String convertValue(Field f, T t) {
        Class<?> type = f.getType();
        f.setAccessible(true);
        Object o = null;
        try {
            o = f.get(t);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated catch block
        return convertLogic(o, type);
    }

    /**
     * Convertie un objet en String formatter pour la BDD.
     *
     * @param o la reponse a convertir<br/>
     * peut etre null.
     * @param type quel type c'est. Boolean.class
     * @return exemple: si o est une java.util.date alors ca retourne =>
     * TO_DATE(xx/yy/zzzz,'dd/mm/yyyy')<br/>
     * si o est un Boolean a vrai => '1'<br/>
     * si o est un string to'to => 'to''to'
     */
    public String convertLogic(Object o, Class<?> type) {
        if (o == null) {
            return "null";
        }

        if (type == Date.class) {
            Date d = (Date) o;
            return toDate(d);
        } else if (type == java.util.Date.class) {
            java.util.Date d = (java.util.Date) o;
            return toDate(d);
        } else if (type == String.class) {
            String tmp = o.toString();
            return "'" + tmp.replaceAll("\'", "\'\'") + "'";
        } else if (type == Boolean.class) {
            Boolean b = (Boolean) o;
            return b ? "'1'" : "'0'";
        } else if (convertLogicCustom(type, o) != null) {
            return convertLogicCustom(type, o);
        }
        return o.toString();
    }

    /**
     * Convertie un type fild custom [genre une enum] pour la BDD
     *
     * @param type
     * @param o
     * @return
     */
    protected abstract String convertLogicCustom(Class<?> type, Object o);

    protected Field[] getFields() {
        return FieldUtils.getAllFields(clazz);
    }

    /**
     * Insert un objet.
     *
     * @param t
     * @throws SQLException
     */
    @Override
    public void insert(T t) throws SQLException {
        StringBuilder into = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Field fields[] = getFields();
        int pos = 0;
        int len = fields.length;
        for (Field field : fields) {
            if (field.getAnnotation(StaticField.class) != null) {
                len--;
                continue;
            }
            into.append(getColoumnName(field));
            values.append(convertValue(field, t));
            if (pos + 1 < len) {
                into.append(',');
                values.append(',');
            }
            pos++;
        }
        String resInto = into.toString();
        if (resInto.endsWith(",")) {
            resInto = resInto.substring(0, resInto.length() - 1);
        }
        String resValues = values.toString();
        if (resValues.endsWith(",")) {
            resValues = resValues.substring(0, resValues.length() - 1);
        }
        String query = "insert into " + getTableName() + " (";
        query += resInto;
        query += ") VALUES (";
        query += resValues + ")";

        st = con.createStatement();
        if (DEBUG_MODE) {
            System.out.println(query);
        }
        st.executeUpdate(query);
        con.commit();
        st.close();
    }

    @Override
    public void insert(List<T> ts) throws SQLException {
        if (ts == null || ts.isEmpty()) {
            return;
        }
        String into = null;
        into = convertFiledsToString(null);
        st = con.createStatement();
        StringBuilder values = null;
        for (T t : ts) {
            values = new StringBuilder();
            for (Field field : getFields()) {
                if (field.getAnnotation(StaticField.class) != null) {
                    continue;
                }
                values.append(convertValue(field, t));
                values.append(',');
            }
            String resValues = values.toString();
            if (resValues.endsWith(",")) {
                resValues = resValues.substring(0, resValues.length() - 1);
            }
            String query = "insert into " + getTableName() + " (";
            query += into;
            query += ") VALUES (";
            query += resValues + ")";
            if (DEBUG_MODE) {
                System.out.println(query);
            }
            st.addBatch(query);
        }
        st.executeBatch();
        con.commit();
        st.close();
    }

    /**
     * Permet de faire un delete "localisé".
     *
     * @param t
     * @throws SQLException
     */
    public void preparedDelete(T t) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(getTableName());
        sb.append(" where ");
        sb.append(preparedPrimaryKey(t));
        st = con.createStatement();
        st.executeUpdate(sb.toString());
        con.commit();
        st.close();
    }

    @Override
    public void close() {
        try {
            if (!st.isClosed()) {
                st.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Il faut faire return preparedUpdate();.
     *
     * @param t
     * @throws SQLException
     */
    @Override
    public void update(T t) throws SQLException {
        preparedUpdate(t, null);
    }

    @Override
    public void update(List<T> t) throws SQLException {
        if (t == null || t.isEmpty()) {
            return;
        }
        st = con.createStatement();
        for (T obj : t) {
            if (DEBUG_MODE) {
                System.out.println(makeUpdate(obj, null));
            }
            st.addBatch(makeUpdate(obj, null));
        }
        st.executeBatch();
        con.commit();
        st.close();
    }

    private String makeUpdate(T obj, String where) {
        StringBuilder sb = new StringBuilder();
        Field fields[] = getFields();
        int pos = 0;
        int len = fields.length;
        for (Field field : fields) {
            if (field.getAnnotation(StaticField.class) != null) {
                len--;
                continue;
            }
            sb.append(getColoumnName(field));
            sb.append("=");
            sb.append(convertValue(field, obj));
            if (pos + 1 < len) {
                sb.append(",");
            }
            pos++;
        }
        String resSb = sb.toString();
        if (resSb.endsWith(",")) {
            resSb = resSb.substring(0, resSb.length() - 1);
        }
        return "UPDATE " + getTableName() + " SET " + resSb + " " + (where == null ? " where " + preparedPrimaryKey(obj) : where);
    }

    public List<Field> getPrimaryKey() {
        Field fs[] = FieldUtils.getAllFields(clazz);
        List<Field> l = new ArrayList<>();
        for (Field ftmp : fs) {
            if (ftmp.getAnnotation(Id.class) != null) {
                l.add(ftmp);
            }
        }
        return l;
    }

    public StringBuilder preparedPrimaryKey(T t) {
        List<Field> l = getPrimaryKey();
        StringBuilder sb = new StringBuilder();
        for (Field f : l) {
            sb.append(getColoumnName(f));
            sb.append(" = ");
            sb.append(convertValue(f, t));
            sb.append(" AND ");
        }
        return sb.delete(sb.length() - 5, sb.length());
    }

    /**
     * Il faut faire return preparedDelete();.
     *
     * @param t
     * @throws SQLException
     */
    @Override
    public void delete(T t) throws SQLException {
        preparedDelete(t);
    }

    @Override
    public void deleteObjectById(Integer id) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE from ");
        sb.append(getTableName());
        sb.append(" where id = ");
        sb.append(id);
        sendSqlUpdate(sb.toString());
    }

    public String toDate(String date) {
        return InterfaceBddFactory.getInterface(this).toDate(date);
    }

    /**
     * Retourne le SQL pour une date. Par defaut le pattern de base est
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public String toDate(java.util.Date date) {
        return InterfaceBddFactory.getInterface(this).toDate(date);
    }

    public String getColoumnName(Field f) {
        String fieldName;
        if (f.getAnnotation(Name.class) != null) {
            fieldName = f.getAnnotation(Name.class).name();
        } else {
            fieldName = convertFieldToChamp(f.getName());
        }
        return fieldName;
    }

    /**
     * Convertie une liste en SQL in.
     *
     * @param ts la liste d'objet peuplé
     * @param fieldName le nom du champ entity qu'on veux transformer.
     * @return mon_champ in (val1,val2,...) ou ""
     */
    public String idToIn(List<T> ts, String fieldName) {
        if (ts == null || ts.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Field f = FieldUtils.getDeclaredField(clazz, fieldName, true);
        sb.append(getColoumnName(f));
        sb.append(" in (");
        int pos = 0;
        for (T t : ts) {
            sb.append(convertValue(f, t));
            if (pos + 1 < ts.size()) {
                sb.append(",");
            }
            pos++;
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public T getObjectById(int id) throws SQLException {
        String sql = "where id = " + id;
        return preparedSelectOnce(sql);
    }

    /**
     * secrured car prepared statement. ne pas oublier de mettre des ? dans le
     * where. puis de remplir la map secureList
     *
     * @param where
     * @param prefix
     * @return
     * @throws SQLException
     */
    public synchronized List<T> securePreparedSelectMulti(String where, String prefix) throws SQLException {
        String query = getFieldsForSelect(prefix) + getTableName() + " " + where + ";";
        PreparedStatement ps = con.prepareStatement(query);
        int i = 1;
        for (Object key : secureList) {
            ps.setObject(i++, key);
        }
        if (DEBUG_MODE) {
            String query2 = query;
            for (Object key : secureList) {
                query2 = query2.replace("?", key.toString());
            }
            System.out.println(query2);
        }
        secureList.clear();
        return fillObjects(ps.executeQuery());
    }

    public synchronized T securePreparedSelectOnce(String where, String prefix) throws SQLException {
        String query = getFieldsForSelect(prefix) + getTableName() + " " + where + ";";
        PreparedStatement ps = con.prepareStatement(query);
        int i = 1;
        for (Object key : secureList) {
            ps.setObject(i++, key);
        }
        if (DEBUG_MODE) {
            String query2 = query;
            for (Object key : secureList) {
                query2 = query2.replaceFirst("\\?", key.toString());
            }
            System.out.println(query2);
        }
        secureList.clear();
        return fillObject(ps.executeQuery());
    }

}
