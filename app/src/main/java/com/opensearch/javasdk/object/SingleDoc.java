package com.opensearch.javasdk.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.json.JSONArray;

import com.opensearch.javasdk.CloudsearchDoc;

/**
 * 一个文档对应的数据
 * 
 * @author 童昭 liushuang.ls@alibaba-inc.com
 * @createDate 2013-12-20
 */
public class SingleDoc {
    private String cmd;
    private Map<String, String> fields = new HashMap<String, String>();

    /**
     * 添加一个属性值
     * 
     * @param key key
     * @param value value
     */
    public void addField(String key, String value) {
        if (value.contains(CloudsearchDoc.HA_DOC_MULTI_VALUE_SEPARATOR)) {
            String[] values = value.split(CloudsearchDoc.HA_DOC_MULTI_VALUE_SEPARATOR);
            JSONArray array = new JSONArray();
            for (String v : values) {
                array.put(v);
            }
            this.fields.put(key, array.toString());
        } else {
            this.fields.put(key, value);
        }
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    /**
     * 获取一条记录的JSON格式
     * 
     * @return 一条记录的JSON格式
     */
    public JSONObject getJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", cmd);
            JSONObject jsonFields = new JSONObject();
            for (Entry<String, String> entry : fields.entrySet()) {
                jsonFields.put(entry.getKey(), entry.getValue());
            }
            json.put("fields", jsonFields);
        } catch (Exception e) {
            // ignore
        }
        return json;
    }
}
