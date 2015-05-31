package com.androider.legacy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Think on 2015/5/31.
 */
public class DateConverter {
    public static SimpleDateFormat formater = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat indexFormater = new SimpleDateFormat("yyyy-MM-dd");
    public static Date toNormalDate(String dateStr){
        try {
            return formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDate(Date date){
        return formater.format(date);
    }

    public static String justDate(Date date){
        return indexFormater.format(date);
    }
}
