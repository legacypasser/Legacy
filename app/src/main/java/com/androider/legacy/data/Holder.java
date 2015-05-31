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
    public static ArrayList<Post> recommendPost = new ArrayList<Post>();
    public static HashMap<Integer, Post> detailed = new HashMap<Integer, Post>();
    public static ArrayList<Post> resultedPost = new ArrayList<Post>();
    public static HashMap<Integer, Session> talks = new HashMap<>();
    public static HashMap<Integer, String> peers = new HashMap<Integer, String>();
    public static HashMap<Long, Record> waitBack = new HashMap<>();

}
