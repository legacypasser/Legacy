package com.opensearch.javasdk.type;

/**
 * schema的字段对应的类型
 * 
 * @author 童昭 liushuang.ls@alibaba-inc.com
 * @createDate 2013-12-18
 */
public enum SchemaTableFieldType {
    INT8("INT8", "INT"), UINT8("UINT8", "INT"), INT16("INT16", "INT"), UINT16("UINT16", "INT"), INT32(
            "INT32", "INT"), UINT32("INT32", "INT"), INT64("INT64", "INT"), UINT64("INT64", "INT"), TEXT(
            "TEXT", "TEXT"), STRING("STRING", "TEXT"), FLOAT("FLOAT", "FLOAT"), DOUBLE("DOUBLE",
            "FLOAT");

    private String type;// 具体的类型
    private String bigType;// 大类型

    SchemaTableFieldType(String type, String bigType) {
        this.type = type;
        this.bigType = bigType;
    }

    /**
     * 获得具体的类型对应的字符串
     * 
     * @return 具体的类型对应的字符串，INT8，INT16...
     */

    public String getType() {
        return type;
    }

    /**
     * 获得粗类型对应的字符串
     * 
     * @return 粗类型对应的字符串 INT,TEXT,FLOAT
     */
    public String getBigType() {
        return bigType;
    }
    
}
