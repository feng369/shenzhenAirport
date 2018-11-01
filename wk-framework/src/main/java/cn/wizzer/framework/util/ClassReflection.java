package cn.wizzer.framework.util;


import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClassReflection {
        private static final Log log = Logs.get();
    /**
     * @param class1 用于赋值的实体类
     * @param class1 需要待赋值的实体类
     * @author koudp
     * @CreateTime 2012-11-22下午03:23:23
     * 描述：反射实体类赋值
     */
    @SuppressWarnings("unchecked")
    public static void reflectionAttr(Object class1,Object class2) throws Exception{
        Class clazz1 = Class.forName(class1.getClass().getName());
        Class clazz2 = Class.forName(class2.getClass().getName());
        //找到这两个类及其父类的所有属性
        List<Field> fieldList1 = new ArrayList<>() ;
        List<Field> fieldList2 = new ArrayList<>() ;
        Class tempClass = clazz1;
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList1.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        tempClass = clazz2;
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList2.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

//		遍历class1Bean，获取逐个属性值，然后遍历class2Bean查找是否有相同的属性，如有相同则赋值
        for (Field f1 : fieldList1) {
//            if(f1.getName().equals("id"))
//                continue;
            Object value = invokeGetMethod(class1 ,f1.getName(),null);
            if(value==null)continue;;
            for (Field f2 : fieldList2) {
                if(f1.getName().equals(f2.getName())){
                    Object[] obj = new Object[1];
                    obj[0] = value;
                    invokeSetMethod(class2, f2.getName(), obj);
                }
            }
        }
    }

    /**
     *
     * 执行某个Field的getField方法
     *
     * @param clazz 类
     * @param fieldName 类的属性名称
     * @param args 参数，默认为null
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object invokeGetMethod(Object clazz, String fieldName, Object[] args) throws Exception {
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
        Method method = null;
//        try
//        {
            Class clazzt = Class.forName(clazz.getClass().getName());
            while(clazzt!=null){
                try {
                    method = clazzt.getDeclaredMethod("get" + methodName);
                    break;
                }catch(NoSuchMethodException e){
                    clazzt = clazzt.getSuperclass();
                }
            }
            if(method==null)return null;
            return method.invoke(clazz);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return null;
//        }
    }

    /**
     *
     * 执行某个Field的setField方法
     *
     * @param clazz 类
     * @param fieldName 类的属性名称
     * @param args 参数，默认为null
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object invokeSetMethod(Object clazz, String fieldName, Object[] args)throws Exception
    {
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);
        Method method = null;
//        try
//        {
            Class[] parameterTypes = new Class[1];
            Class c = Class.forName(clazz.getClass().getName());
            Field field = null;
            while(c!=null) {
                try {
                    field = c.getDeclaredField(fieldName);
                    break;
                }catch (NoSuchFieldException e){

                    c = c.getSuperclass();
                }
            }
            if(field==null)return null;
            parameterTypes[0] = field.getType();
            method = c.getDeclaredMethod("set" + methodName,parameterTypes);
            return method.invoke(clazz,args);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return "";
//        }
    }


}
