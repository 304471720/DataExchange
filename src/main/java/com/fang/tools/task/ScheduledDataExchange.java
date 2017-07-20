package com.fang.tools.task;

import com.fang.tools.base.DataExchangeBase;
import com.fang.tools.util.DaoUtil;
import com.fang.tools.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by user on 2017/7/12.
 */
@Component
public class ScheduledDataExchange {
    private final Logger logger = LoggerFactory.getLogger(ScheduledDataExchange.class);
    @Autowired
    @Qualifier("fromJdbcTemplate")
    protected JdbcTemplate jdbcTemplate1;
    @Autowired
    @Qualifier("toJdbcTemplate")
    protected JdbcTemplate jdbcTemplate2;

    @Autowired
    @Qualifier("fromDataSource")
    protected DataSource fromDataSourceMsSql;

    @Autowired
    @Qualifier("toDataSource")
    protected DataSource toDataSourceMySql;

    @Scheduled(cron = "0 10 16 * * ?")
    public void deleteUnregistTokens() {
        Connection toConn = null;

        try {
            toConn = toDataSourceMySql.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.info(toConn.toString());
        DaoUtil deleDao = new DaoUtil(toConn);
        deleDao.update("delete  sendUser where token in ");
        deleDao.close();
        logger.info(toConn.toString());

    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void test() {
        Connection fromConn = null;
        Connection toConn = null;
        try {
            fromConn = fromDataSourceMsSql.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            toConn = toDataSourceMySql.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.info(fromConn.toString());
        logger.info(toConn.toString());
        String fromTablename = String.format("homepush_App_Ipad_%s", StringUtil.getDateByOffset("yyyyMMdd", -1));
        new DataExchangeBase(fromConn, toConn)
                .setFromSelectSql("select  Imei,City,'Identity',Detail,LogDate  from " + fromTablename)
                .setToInsertHeaderSql("insert into homepush_App(imei,city,role,detail,logdate) values")
                .setClearToSql(" DELETE    FROM homepush_App WHERE imei != 'f9fb41113d964208d7f012500d03223bde785635' ")
                //.setIfPreparedSql("SELECT Finished  FROM PushRecordsQueues WHERE  Finished = '4' and BizName = 'home_push_messagename' and LogDate = '"+StringUtil.getDateByOffset("yyyyMMdd", -1)+"'")
                .setBegin(true)
                .setPagesize(200000)
                .doExchange();
        logger.info(jdbcTemplate1.queryForObject("SELECT count(*) from " + fromTablename, String.class));
        logger.info(jdbcTemplate2.queryForObject("SELECT count(*) from  homepush_App  ", String.class));
        logger.info(fromConn.toString());
        logger.info(toConn.toString());

    }
}
