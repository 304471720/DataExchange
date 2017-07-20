package com.fang.tools.util;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 2017/3/24.
 */
public class DaoUtil {
    private  Logger logger = Logger.getLogger(DaoUtil.class);
    private Connection conn = null;
    private PreparedStatement prepstmt = null;
    private ResultSet rs = null;

    public DaoUtil(Connection conn)
    {
        this.conn = conn ;
    }
    public  <T> List<T> getList(String sSql, Class<T> clazz)
    {
        return getList(sSql,clazz,null);
    }

    public  <T> List<T> getList(String sSql, Class<T> clazz, Processor<T> run) {
        if (conn == null)
        {
            return  null;
        }
        List<T> ret = new ArrayList<T>();
        PreparedStatement prepstmt = null;
        ResultSet rs = null;
        try {
            StopWatch sw = new StopWatch(clazz.getName()+" query ");
            sw.start("Connection");
            sw.stop();
            sw.start("executeQuery");
            //prepstmt = conn.prepareStatement(sSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            prepstmt = conn.prepareStatement(sSql);
            //prepstmt.setFetchSize(Integer.MIN_VALUE);
            //prepstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            rs = prepstmt.executeQuery();
            sw.stop();
            sw.start(" while ResultSet start ");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                T object = clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    ReflectUtil.setPrivateField(object,columnName,rs.getObject(columnName));
                }
                if (run!=null)
                {
                    run.run(object);
                }else
                {
                    ret.add(object);
                }
            }
            sw.stop();
            logger.info(sw.prettyPrint());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ret = null;
        } /*finally {
            close(conn, prepstmt, rs);
        }*/
        return ret;
    }

    public   List<List<Object>> getList(String sSql) {
        return getList(sSql,(Processor)null);
    }
    public   List<List<Object>> getList(String sSql, Processor<List<Object>> run) {
        if (conn == null)
        {
            return  null;
        }
        List<List<Object>> ret = new ArrayList<List<Object>>();
        PreparedStatement prepstmt = null;
        ResultSet rs = null;
        try {
            StopWatch sw = new StopWatch(" query ");
            sw.start("Connection");
            sw.stop();
            sw.start("executeQuery");
            //prepstmt = conn.prepareStatement(sSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            logger.info(sSql);
            prepstmt = conn.prepareStatement(sSql);
            //prepstmt.setFetchSize(Integer.MIN_VALUE);
            //prepstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            rs = prepstmt.executeQuery();
            sw.stop();
            sw.start(" while ResultSet start ");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                List<Object> objectList = new LinkedList<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    objectList.add(rs.getObject(i));
                }
                if (run!=null)
                {
                    run.run(objectList);
                }else
                {
                    ret.add(objectList);
                }
            }
            sw.stop();
            logger.info(sw.prettyPrint());
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            ret = null;
        } /*finally {
            close(conn, prepstmt, rs);
        }*/
        return ret;
    }

    public <T> List<String> get1ColumnFromList(List<T> objectList, String propertyname) {
        List<String> ret = new LinkedList<String>();
        for (T tmp : objectList)
        {
            Class<?> t = ReflectUtil.getPrivateFieldType(tmp,propertyname);
            if (t == null)
            {
                return  null;
            }
            String value = null;
            if (t.getName().equals(String.class.getName()))
            {
                value = (String)ReflectUtil.getPrivateField(tmp,propertyname);
            }else
            {
                value = String.valueOf(ReflectUtil.getPrivateField(tmp,propertyname));
            }
            ret.add(value);
        }
        return ret;
    }

    public  boolean save(final String sSqlTemplate, Object... param) {
        if (conn == null)
        {
            return false;
        }
        String sql =  JdbcUtil.setParam(sSqlTemplate,param);
        logger.info("sql "+sql);
        if (Strings.isNullOrEmpty(sql))
        {
            return false;
        }
        Connection conn = null;
        PreparedStatement prepstmt = null;
        boolean bRet = true;
        try {
            prepstmt = conn.prepareStatement(sql);
            bRet = prepstmt.executeUpdate() == 0 ? false : true;
            logger.info(bRet);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            bRet = false;
        } finally {
            close(conn, prepstmt, null);
        }
        return bRet;
    }

    public  boolean update(String sSql) {
        boolean bRet = true;
        try {
            prepstmt = conn.prepareStatement(sSql);
            logger.debug(sSql.toString());
            bRet = prepstmt.executeUpdate() == 0 ? false : true;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            bRet = false;
        }
        return bRet;
    }

    public  void close(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (st != null)
                st.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  void close() {
        close(conn,prepstmt,rs);
    }
}
