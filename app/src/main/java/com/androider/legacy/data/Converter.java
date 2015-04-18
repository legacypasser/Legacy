package com.androider.legacy.data;

import java.util.ArrayList;

/**
 * Created by Think on 2015/4/18.
 */
public interface Converter {
    public String objectToString(Cachable obj);
    public String listToString(ArrayList<Cachable> objs);
    public ArrayList<Cachable> stringToList(String str);
    public Cachable stringToObj(String str);
}
