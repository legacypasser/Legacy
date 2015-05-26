package com.opensearch.javasdk.object;

import java.util.ArrayList;
import java.util.List;

import com.opensearch.javasdk.type.SchemaTableFieldType;

/**
 * 一个table的一个字段
 * 
 * @author 童昭 liushuang.ls
 * @createDate 2013-12-18
 */
public class SchemaTableField {
    
    private String fieldName;// 字段的名称
    private SchemaTableFieldType type;// 字段的类型
    private boolean primarykey;// 是否是主键
    private boolean multi;// 是否是多值
    private boolean filter;// 是否可以过滤
    private boolean search;// 是否可以搜索
    private boolean display = true;// 是否可以展示，默认可展示
    private boolean aggregate;// 是否可以聚合
    private String outerTable;// 如果定义了此值，则这个字段会和指定的表的primary key字段关联
    private List<String> indexList = new ArrayList<String>();// 指定字段索引字段名称，如果未指定，TEXT类型默认为default，其他默认为字段名称。可以为单值或者数组。

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public SchemaTableFieldType getType() {
        return type;
    }

    public void setType(SchemaTableFieldType type) {
        this.type = type;
    }

    public boolean isPrimarykey() {
        return primarykey;
    }

    public void setPrimarykey(boolean primarykey) {
        this.primarykey = primarykey;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isAggregate() {
        return aggregate;
    }

    public void setAggregate(boolean aggregate) {
        this.aggregate = aggregate;
    }

    public String getOuterTable() {
        return outerTable;
    }

    public void setOuterTable(String outerTable) {
        this.outerTable = outerTable;
    }

    public List<String> getIndexList() {
        return indexList;
    }

    /**
     * 添加一个索引项
     * @param indexStr
     */
    public void addIndex(String indexStr) {
        if (indexStr == null || indexStr.trim().equals("")) {
            return;
        }
        for (String existIndex : indexList) {
            if (indexStr.equals(existIndex)) {
                return;
            }
        }
        indexList.add(indexStr);
    }
}
