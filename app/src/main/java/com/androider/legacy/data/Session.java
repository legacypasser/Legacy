package com.androider.legacy.data;

import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Session implements Cachable{
    public int peer;
    public String records;
    public static LinkedList<Session> sessions = new LinkedList<Session>();

    public Session(int peer, String records) {
        this.peer = peer;
        this.records = records;
    }

    public LinkedList<Record> getRecords(){
        LinkedList<Record> certain = new LinkedList<Record>();
        String[] peerList =  records.split(Constants.regex);
        for(String item : peerList){
            certain.add(Record.records.get(item));
        }
        return certain;
    }

    public void addRecord(String id){
        if(records == "")
            records += id;
        else
        records += Constants.regex + id;
    }

    @Override
    public Cachable get(String url) {
        return null;
    }

    @Override
    public LinkedList<Cachable> getList(String url) {
        return null;
    }
}
