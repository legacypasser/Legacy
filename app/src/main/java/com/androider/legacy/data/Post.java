package com.androider.legacy.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Post implements Cachable{
    public int id;
    public String des;
    public String img;
    public int seller;
    public Date publish;
    public String abs;

    public static ArrayList<Post> myPost = new ArrayList<Post>();
    public static ArrayList<Post> recommendPost = new ArrayList<Post>();


    public Post(int id, String des, String img, int seller, Date publish, String abs) {
        this.id = id;
        this.des = des;
        this.img = img;
        this.seller = seller;
        this.publish = publish;
        this.abs = abs;
    }

    @Override
    public Cachable get(String url) {
        return null;
    }

    @Override
    public ArrayList<Cachable> getList(String url) {
        int pageLimit = Integer.parseInt(url.split(Constants.connnector)[1]);
        int totalNumber = pageLimit * Constants.eachPage;
        if(recommendPost.size() >= totalNumber){

        }
    }

    private LinkedList<Post> getBaseList(String url){
        
    }

    private LinkedList<Post> getNetList(String url){

    }

}
