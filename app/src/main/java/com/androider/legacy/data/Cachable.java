package com.androider.legacy.data;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public interface Cachable {
    public Cachable get(String url);
    public ArrayList<Cachable> getList(String url);

}
