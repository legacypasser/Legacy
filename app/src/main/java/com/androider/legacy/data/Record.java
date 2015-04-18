package com.androider.legacy.data;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Record implements Cachable{
    public int id;
    public int sender;
    public int receiver;
    public String content;
    public String edit;

    public static HashMap<String, Record> records = new HashMap<String, Record>();

    public Record(int id, int sender, int receiver, String content, String edit) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.edit = edit;
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
