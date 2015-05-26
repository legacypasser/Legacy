package com.opensearch.javasdk.object;

import java.util.ArrayList;
import java.util.List;

import com.opensearch.javasdk.type.SchemaTableFieldType;

/**
 * schema的一个表
 * 
 * @author 童昭
 * @createDate 2013-12-18
 */
public class SchemaTable {

    private String tableName;// schema的表的名称
    private boolean masterTable;// 是否是主表

    // schema的属性
    private List<SchemaTableField> fieldList = new ArrayList<SchemaTableField>();

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * add a field of schemaTable
     * 
     * @param schemaTableField
     */
    public void addField(SchemaTableField schemaTableField) {
        if (schemaTableField == null) {
            return;
        }
        if (schemaTableField.isPrimarykey()) {
            schemaTableField.setSearch(true);
            schemaTableField.addIndex(schemaTableField.getFieldName());
            if (schemaTableField.getType().getType().equals(SchemaTableFieldType.TEXT.getType())) {
                // 如果是文字类型的
                schemaTableField.setFilter(false);
                schemaTableField.setAggregate(false);
            } else if (schemaTableField.getType().getType().equals(SchemaTableFieldType.STRING.getType())) {
                schemaTableField.setFilter(false);
            }
        }

        // 检查TEXT类型的属性信息
        if (schemaTableField.getType().getType().equals(SchemaTableFieldType.TEXT.getType())) {
            schemaTableField.setAggregate(false);
            schemaTableField.setFilter(false);
            if (schemaTableField.getIndexList().size() == 0) {
                schemaTableField.addIndex("default");
            } else {
                for (String indexStr : schemaTableField.getIndexList()) {
                    if (!indexStr.matches("^[_a-zA-Z][a-zA-Z0-9_]*")) {
                        return;// 不满足要求
                    }
                }
            }
        }

        // 检查STRING类型的属性信息
        if (schemaTableField.getType().getType().equals(SchemaTableFieldType.STRING.getType())) {
            if (!schemaTableField.isMulti()) {
                schemaTableField.setAggregate(false);
            }
            schemaTableField.setFilter(false);
            if (schemaTableField.getIndexList().size() == 0) {
                schemaTableField.addIndex(schemaTableField.getFieldName());
            } else {
                for (String indexStr : schemaTableField.getIndexList()) {
                    if (!indexStr.matches("^[_a-zA-Z][a-zA-Z0-9_]*")) {
                        return;// 不满足要求
                    }
                }
            }
        }

        // 检查数字类型的属性信息
        if (schemaTableField.getType().getType().equals(SchemaTableFieldType.FLOAT.getType())
                || schemaTableField.getType().getType().equals(SchemaTableFieldType.DOUBLE.getType())
                || schemaTableField.getType().getBigType().equals(SchemaTableFieldType.INT8.getBigType())) {
            schemaTableField.addIndex(schemaTableField.getFieldName());
            if (!schemaTableField.isMulti()) {
                schemaTableField.setFilter(false);
            }
        }

        this.fieldList.add(schemaTableField);
    }

    public List<SchemaTableField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<SchemaTableField> fieldList) {
        this.fieldList = fieldList;
    }

    public boolean isMasterTable() {
        return masterTable;
    }

    public void setMasterTable(boolean masterTable) {
        this.masterTable = masterTable;
    }
}
