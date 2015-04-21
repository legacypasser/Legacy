package com.androider.legacy.data;

import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Session {
    public int peer;
    public String nickname;
    public String records;

    public static final String tableName = "session";
    public Session(int peer, String records) {
        this.peer = peer;
        this.records = records;
    }

    public LinkedList<Record> getRecords(){
        LinkedList<Record> certain = new LinkedList<Record>();
        String[] peerList =  records.split(Constants.regex);
        for(String item : peerList){
            certain.add(Holder.records.get(item));
        }
        return certain;
    }

    public void addRecord(String id){
        if(records == "")
            records += id;
        else
        records += Constants.regex + id;
    }

}
