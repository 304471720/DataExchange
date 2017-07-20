package com.fang.tools.bean;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.List;

/**
 * Created by user on 2017/7/13.
 */
public class SimpleTableStruct {
    private String tableName;
    private List<String> fieldList;
    private String selectSql;
    private String insertSql;

    private final String selectTemplate= " select %s from  %s";
    private final String insertTemplate = " insert into %s(%s) values ";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }

    public SimpleTableStruct(String tableName, List<String> fieldList) {
        this.tableName = tableName;
        this.fieldList = fieldList;
    }

    public String getSelectSql() {
        if (selectSql != null)
        {
            return  selectSql;

        }else if (fieldList ==null || fieldList.size() <= 0 || Strings.isNullOrEmpty(tableName))
        {
            return null;
        }else
        {
            return  String.format(selectTemplate, Joiner.on(",").skipNulls().join(fieldList), tableName);
        }
    }
    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public String getInsertSql() {
        if (insertSql != null)
        {
            return  insertSql;
        }else if (fieldList ==null || fieldList.size() <= 0 || Strings.isNullOrEmpty(tableName))
        {
            return null;
        }else
        {
            return  String.format(insertTemplate, tableName,Joiner.on(",").skipNulls().join(fieldList));
        }
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }
}
