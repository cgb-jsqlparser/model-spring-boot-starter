package com.cet.eem.model.base.metadata;

/**
 * @author ACloud
 * @title: DataType
 * @description: datatype
 * @date 2019/4/916:50
 */
public class DataType {
    public static final String INT4 ="int4";
    public static final String INT8="int8";
    public static final String FLOAT="float";
    public static final String STRING = "string";
    public static final String BOOLEAN = "boolean";
    public static final String JSON_B= "jsonb";
    public static final String DATE= "date";

    public static boolean equalsBaseDataType(String dataType){
        return dataType.equalsIgnoreCase(INT4)
                || dataType.equalsIgnoreCase(INT8)
                || dataType.equalsIgnoreCase(FLOAT)
                || dataType.equalsIgnoreCase(STRING)
                || dataType.equalsIgnoreCase(BOOLEAN)
                || dataType.equalsIgnoreCase(JSON_B);
    }

    public static Object parseDefaultValue(String dataType,String defaultValue){
        if(dataType.equalsIgnoreCase(INT4)){
            try {
                return Integer.parseInt(defaultValue);
            }
            catch(NumberFormatException ex){
                return 0;
            }

        }
        else if(dataType.equalsIgnoreCase(INT8)){
            try{
                return Long.parseLong(defaultValue);
            }
            catch(NumberFormatException ex){
                return 0;
            }

        }
        else if(dataType.equalsIgnoreCase(FLOAT)){
            try{
                return Double.parseDouble(defaultValue);
            }
            catch(NumberFormatException ex){
                return 0.0;
            }
        }
        else if(dataType.equalsIgnoreCase(BOOLEAN)){
            if(defaultValue == null){
                return false;
            }
            return Boolean.parseBoolean(defaultValue);
        }
        else if(dataType.equalsIgnoreCase(STRING)){
            return defaultValue;
        }
        // 默认值只针对普通类型
        else if(dataType.equalsIgnoreCase(JSON_B)){
            return new Object();
        }
        // 枚举类型
        return 1;
    }
}
