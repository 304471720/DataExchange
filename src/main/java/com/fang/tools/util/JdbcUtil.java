package com.fang.tools.util;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 2017/3/22.
 */
public  class JdbcUtil {
    private static Logger logger = Logger.getLogger(JdbcUtil.class);
    public static final String SQL_TEMPLATE_TAG=":";
    public static final String SQL_TEMPLATE_TAG_PATTERN_WITHNAME=":\\w+";
    public static final char SQL_TAG_PATTERN='?';

    public static String setParam(String sqlTemplate , String tag, Object param)
    {
        String ret= null;
        if (Strings.isNullOrEmpty(sqlTemplate) || param == null || Strings.isNullOrEmpty(tag))
        {
            return null;
        }
        if (param  instanceof String)
        {
            ret = sqlTemplate.replaceAll(SQL_TEMPLATE_TAG+tag, String.format("'%s'",param));
        }else if (param instanceof Iterable)
        {
            Iterable aList = (Iterable)param;
            List<String> aTmp = new ArrayList<String>();
            for(Object tmp : aList)
            {
                if (tmp instanceof String)
                {
                    aTmp.add(String.format("'%s'",tmp));
                }else
                {
                    aTmp.add(String.format("%s",tmp));
                }
            }
            String toString = Joiner.on(",").skipNulls().join(aTmp);
            ret = sqlTemplate.replaceAll(SQL_TEMPLATE_TAG_PATTERN_WITHNAME,toString);
        }
        return ret;
    }

    public static String setParam(String sqlTemplate, Object... param)
    {
        if (Strings.isNullOrEmpty(sqlTemplate) ||  param.length ==0)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer(sqlTemplate.length());
        Integer matchcount= -1;
        for (int i = 0; i < sqlTemplate.length(); i++)
        {
            if (sqlTemplate.charAt(i) == SQL_TAG_PATTERN)
            {
                matchcount++;
                if (matchcount > param.length-1 )
                {
                    return null;
                }
                if (param[matchcount] instanceof String)
                {
                    sb.append("'"+param[matchcount]+"'");
                }else
                {
                    sb.append(param[matchcount]);
                }
            }else
            {
                sb.append(sqlTemplate.charAt(i));
            }
        }
        return sb.toString();
    }


    public static void main(String[] args)
    {
/*        String sql = " SELECT * FROM   new_sendHuoDong " ;
        List<SendHuoDongEntity> ll = jdbcUtil.getListFromDb(sql,SendHuoDongEntity.class);
        logger.info(ll.size());*/
       /* SendHuoDongEntity sendHuoDongEntity = new SendHuoDongEntity();
        sendHuoDongEntity.setCity("test");
        sendHuoDongEntity.setEndtime("xxxxxxx");
        sendHuoDongEntity.setStarttime("xxxxxxxxxxx");
        sendHuoDongEntity.setCompany("xxxxx");
        sendHuoDongEntity.setCont("xxxx");
        sendHuoDongEntity.setDest("xxx");
        sendHuoDongEntity.setFlag(1);
        sendHuoDongEntity.setHuoDonguser("xxxx");
        String sql = "   insert into new_sendHuoDong (id,company,title ,url,city,starttime,endtime,insertTime)  " +
                "   values(?,?,?,?,?,?,?,?)  ";
        try {
            logger.info( jdbcUtil.save(sql,
                    sendHuoDongEntity.getId(),
                    "全部",
                    sendHuoDongEntity.getTitle(),
                    sendHuoDongEntity.getUrl(),
                    sendHuoDongEntity.getCity(),
                    sendHuoDongEntity.getStarttime(),
                    sendHuoDongEntity.getEndtime(),
                    sendHuoDongEntity.getInserttime()) );
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }*/

        final String valuesStr = "(:entityAttr)";
        final List<String> value = new LinkedList<String>();
        value.add("sssss");
        value.add("bbbbbbb");
        value.add("cccccccccc");
        logger.info(JdbcUtil.setParam(valuesStr,"entityAttr",value));
    }
}
