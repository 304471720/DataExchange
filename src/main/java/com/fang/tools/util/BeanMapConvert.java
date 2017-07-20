package com.fang.tools.util;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by user on 2017/3/15.
 */
public class BeanMapConvert {

    public static <T> T map2Bean(Map map, Class<T> class1) {
        T bean = null;
        try {
            bean = class1.newInstance();
            BeanUtils.populate(bean, map);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }
}