package com.androider.legacy.data;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

/**
 * Created by Think on 2015/4/19.
 */
public class Holder{
    public static ArrayList<Post> myPost = new ArrayList<Post>();
    public static ArrayList<Post> recommendPost = new ArrayList<Post>();
    public static HashMap<Integer, Post> detailed = new HashMap<Integer, Post>();
    public static ArrayList<Post> resultedPost = new ArrayList<Post>();

    public static HashMap<Integer, Session> talks = new HashMap<>();

    public static HashMap<Integer, Record> records = new HashMap<Integer, Record>();

    public static HashMap<Integer, String> peers = new HashMap<Integer, String>();

    public static ArrayList<String> paths = new ArrayList<>();

    public static String publishDes;

    public static ArrayList<Record> justReceived;

    public static HashMap<Record, Session> belongTo = new HashMap<>();

}
