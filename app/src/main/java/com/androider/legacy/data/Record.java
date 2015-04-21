package com.androider.legacy.data;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Record {
    public int id;
    public int sender;
    public int receiver;
    public String content;
    public Date edit;

    public static final String tableName = "record";

    public Record(int id, int sender, int receiver, String content, Date edit) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.edit = edit;
    }

}
