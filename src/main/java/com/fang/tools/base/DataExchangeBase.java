package com.fang.tools.base;

import com.fang.tools.bean.SimpleTableStruct;
import com.fang.tools.util.DaoUtil;
import com.fang.tools.util.JdbcUtil;
import com.fang.tools.util.Processor;
import com.fang.tools.util.StringUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by user on 2017/7/13.
 */
public  final class DataExchangeBase {
    private  final Logger logger = LoggerFactory.getLogger(DataExchangeBase.class);
    private  final String valuesTemplate = "(:entityAttr)";
    private static final String  VALUES_SPLIT_CHAR=",";
    private static  final Integer PAGE_SIZE= 50000;
    private  Integer pagesize ;
    private Connection fromConn;
    private Connection toConn;
    private String fromSelectSql;
    private String toInsertHeaderSql;

    private  DaoUtil toDao ;
    private  DaoUtil fromDao ;

    private boolean begin = false;

    private String clearToSql ;
    private String ifPreparedSql;

    public String getIfPreparedSql() {
        return ifPreparedSql;
    }

    public DataExchangeBase setIfPreparedSql(String ifPreparedSql) {
        this.ifPreparedSql = ifPreparedSql;
        return  this;
    }

    public String getClearToSql() {
        return clearToSql;
    }

    public DataExchangeBase setClearToSql(String clearToSql) {
        this.clearToSql = clearToSql;
        return this;
    }

    public boolean isBegin() {
        return begin;
    }

    public DataExchangeBase setBegin(boolean begin) {
        this.begin = begin;
        return  this;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public DataExchangeBase setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
        return this;
    }

    public String getFromSelectSql() {
        return fromSelectSql;
    }

    public DataExchangeBase setFromSelectSql(String fromSelectSql) {
        this.fromSelectSql = fromSelectSql;
        return this;
    }

    public String getToInsertHeaderSql() {
        return toInsertHeaderSql;
    }

    public DataExchangeBase setToInsertHeaderSql(String toInsertHeaderSql) {
        this.toInsertHeaderSql = toInsertHeaderSql;
        return this;
    }

    public DataExchangeBase(Connection fromConn, Connection toConn, String fromTableName, List<String> fromFieldList, String fromSelectSql, String toTableName, List<String> toFieldList, String toInsertHeaderSql, Integer pagesize)
    {
        if (fromConn == null || toConn == null)
        {
            return;
        }
        this.fromConn = fromConn;
        this.toConn = toConn;
        toDao = new DaoUtil(toConn);
        fromDao = new DaoUtil(fromConn);
        if (fromTableName == null || fromFieldList==null)
        {
            this.fromSelectSql = fromSelectSql;
        }else {
            this.fromSelectSql = new SimpleTableStruct(fromTableName,fromFieldList).getSelectSql();
        }
        if (toTableName == null || toFieldList == null )
        {
            this.toInsertHeaderSql = toInsertHeaderSql;
        }else
        {
            this.toInsertHeaderSql = new SimpleTableStruct(toTableName,toFieldList).getInsertSql();
        }
        if (pagesize == null || pagesize <=0)
        {
            this.pagesize = PAGE_SIZE;
        }else
        {
            this.pagesize = pagesize;
        }
    }
    public  DataExchangeBase(Connection fromConn,Connection toConn,String fromSelectSql,String toInsertHeaderSql)
    {
        this(fromConn,toConn,null,null,fromSelectSql,null,null,toInsertHeaderSql,PAGE_SIZE);
    }

    public  DataExchangeBase(Connection fromConn,Connection toConn)
    {
        this(fromConn,toConn,null,null,null,null,null,null,PAGE_SIZE);
    }


    public DataExchangeBase(Connection fromConn,Connection toConn,String fromTableName,List<String> fromFieldList,String toTableName,List<String> toFieldList)
    {
        this(fromConn,toConn,fromTableName,fromFieldList,null,toTableName,toFieldList,null,PAGE_SIZE);
    }

    public void  doExchange()
    {
        if (!begin)
        {
            logger.info("  not begin ");
            return;
        }
        if (!Strings.isNullOrEmpty(ifPreparedSql))
        {
            List<List<Object>> rss = fromDao.getList(ifPreparedSql);
            if (rss == null || rss.size() <= 0 )
            {
                logger.info(" data no be prepared ");
                fromDao.close();
                return;
            }
        }
        if (!Strings.isNullOrEmpty(clearToSql) && !toDao.update(clearToSql))
        {
            logger.info("  clearToSql fails check reason ... ");
            return;
        }
        final List<Object> valueList = new LinkedList<Object>();
        final StringBuilder sb = new StringBuilder(toInsertHeaderSql);
        final AtomicInteger lineCount = new AtomicInteger(0);
        logger.info("fromSelectSql " + fromSelectSql);
        logger.info("toInsertHeaderSql " + toInsertHeaderSql);
        logger.info("doExchange  begin ");
        fromDao.getList(fromSelectSql, new Processor<List<Object>>() {
            @Override
            public void run(List<Object> strings) {
                valueList.addAll(strings);
                lineCount.getAndIncrement();
                if (lineCount.get() % pagesize == 0 )
                {
                    sb.append(JdbcUtil.setParam(valuesTemplate,valuesTemplate.replace(JdbcUtil.SQL_TEMPLATE_TAG,""),valueList));
                    logger.info("lineCount :" + lineCount.get());
                    if (logger.isDebugEnabled())
                    {
                        logger.debug(sb.toString());
                    }
                    toDao.update(sb.toString());
                    sb.setLength(toInsertHeaderSql.length());
                }else
                {
                    sb.append(JdbcUtil.setParam(valuesTemplate,valuesTemplate.replace(JdbcUtil.SQL_TEMPLATE_TAG,""),valueList)).append(VALUES_SPLIT_CHAR);
                }
                valueList.clear();
            }
        });
        if (sb.length() > valuesTemplate.length())
        {
            toDao.update(sb.toString().substring(0,sb.length() - VALUES_SPLIT_CHAR.length()));
            sb.setLength(0);
            toDao.close();
        }
        fromDao.close();
        logger.info("doExchange  end ");
    }

    public static void main(String[] args )
    {
        Connection fromConn=null;
        Connection toConn=null;
        String fromTablename = String.format("homepush_App_Ipad_%s", StringUtil.getDateByOffset("yyyyMMdd",-1));
        List<String> fromFieldList = Arrays.asList("Imei","City","'Identity'","Detail","LogDate");
        String toTableName = "homepush_App";
        List<String> toFieldList = Arrays.asList("imei","city","role","detail","logdate");
        new DataExchangeBase(fromConn,toConn,fromTablename,fromFieldList,toTableName,toFieldList).doExchange();
        //new DataExchangeBase(fromConn,toConn).setFromSelectSql("select  Imei,City,'Identity',Detail,LogDate  from "+fromTablename).setToInsertHeaderSql("insert into homepush_App(imei,city,role,detail,logdate) values").doExchange();
    }
}
