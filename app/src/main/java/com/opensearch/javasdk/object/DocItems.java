package com.opensearch.javasdk.object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

/**
 * 一个文档对应的数据
 * 
 * @author 童昭 liushuang.ls@alibaba-inc.com
 */
public class DocItems {
    private List<SingleDoc> docList = new ArrayList<SingleDoc>(); 
    
    /**
     * 添加一行记录
     * @param doc 记录
     */
    public void addDoc(SingleDoc doc){
        this.docList.add(doc);
    }
    
    public String getJSONString(){
        JSONArray array = new JSONArray();
        for(SingleDoc doc : docList){
            array.put(doc.getJSONObject());
        }
        return array.toString();
    }
}
