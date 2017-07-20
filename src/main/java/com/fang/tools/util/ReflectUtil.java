package com.fang.tools.util;

import java.lang.reflect.Field;

/**
 * Created by user on 2017/3/3.
 */
public class ReflectUtil {

    public static Object getPrivateField(final Object Object, final String fieldName)
    {
        try {
            Class<?> clazz = Object.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(Object);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getPrivateFieldType(final Object Object, final String fieldName)
    {
        try {
            Class<?> clazz = Object.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getType();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Object setPrivateField(final Object Object, final String fieldName, final Object value)
    {
        try {
            Class<?> classType = Object.getClass();
            Field field = classType.getDeclaredField(fieldName);
            field.setAccessible(true); // 抑制Java对修饰符的检查
            field.set(Object, value);
        } catch (SecurityException e) {
            //e.printStackTrace();
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
        }catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }
        return null;
    }
}
