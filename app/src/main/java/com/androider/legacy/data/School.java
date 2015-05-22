package com.androider.legacy.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Think on 2015/5/22.
 */
public class School {
    public int id;
    public String name;
    public String majors;
    public static HashMap<String, School> maybeUsed;
    public ArrayList<String> majorlist;
    public ArrayList<String> getMajors(){
        if(majors.equals(""))
            return null;
        majorlist = new ArrayList<>();
        Cursor cursor = MainActivity.db.query("major", new String[]{"name"}, "id in (" + majors + ")", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            majorlist.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return majorlist;
    }

    public static ArrayList<School> regional(int region){
        Cursor cursor = MainActivity.db.rawQuery("select * from school where region = ?;", new String[]{"" + region});
        ArrayList<School> schools = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            School one = new School();
            one.id = cursor.getInt(cursor.getColumnIndex("id"));
            one.name = cursor.getString(cursor.getColumnIndex("name"));
            one.majors = cursor.getString(cursor.getColumnIndex("major"));
            schools.add(one);
            cursor.moveToNext();
        }
        cursor.close();
        return schools;
    }

    public static ArrayList<String> regional(String province){
        Cursor cursor = MainActivity.db.rawQuery("select id from region where name = ?;", new String[]{province});
        cursor.moveToFirst();
        ArrayList<School> schools = regional(cursor.getInt(0));
        cursor.close();
        ArrayList<String> result = new ArrayList<>();
        maybeUsed = new HashMap<>();
        for(School item : schools){
            maybeUsed.put(item.name, item);
            result.add(item.name);
        }
        return result;
    }

    public static ArrayList<String> prefixed(String pre){
        Cursor cursor = MainActivity.db.rawQuery("select * from school where name like ?;", new String[]{pre + "%"});
        ArrayList<String> schools = new ArrayList<>();
        maybeUsed = new HashMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            School one = new School();
            one.id = cursor.getInt(cursor.getColumnIndex("id"));
            one.name = cursor.getString(cursor.getColumnIndex("name"));
            one.majors = cursor.getString(cursor.getColumnIndex("major"));
            schools.add(one.name);
            maybeUsed.put(one.name, one);
            cursor.moveToNext();
        }
        cursor.close();
        return schools;
    }

    public static void iniSchool(Context context){
        Cursor cursor = MainActivity.db.rawQuery("select * from School;", null);
        int judger = cursor.getCount();
        if(judger != 0){
            cursor.close();
            return;
        }
        AssetManager manager = context.getAssets();
        MainActivity.db.beginTransaction();
        try {
            InputStream in = manager.open("majorbase.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String str;
            ContentValues cv;
            while ((str = reader.readLine()) != null){
                String[] majorFields = str.split(";");
                cv = new ContentValues();
                if(majorFields.length == 1){
                    cv.put("id", Integer.parseInt(majorFields[0]));
                    cv.put("name", "");
                }else{
                    cv.put("id", Integer.parseInt(majorFields[1]));
                    cv.put("name", majorFields[0]);
                }
                MainActivity.db.insert("major", null, cv);
            }
            reader.close();
            in.close();
            in = manager.open("regionbase.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            while ((str = reader.readLine()) != null){
                String[] schoolFields = str.split(";");
                cv = new ContentValues();
                cv.put("name", schoolFields[0]);
                cv.put("id", Integer.parseInt(schoolFields[1]));
                MainActivity.db.insert("region", null, cv);
            }
            reader.close();
            in.close();

            in = manager.open("schoolbase.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            while ((str = reader.readLine()) != null){
                String[] schoolFields = str.split(";");
                cv = new ContentValues();
                cv.put("region", Integer.parseInt(schoolFields[1]));
                cv.put("name", schoolFields[0]);
                if(schoolFields.length == 3)
                    cv.put("major", schoolFields[2]);
                MainActivity.db.insert("school", null, cv);
            }
            reader.close();
            in.close();
            MainActivity.db.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            MainActivity.db.endTransaction();
        }
    }

    public ArrayList<String> prefixedMajors(String pre){
        ArrayList<String> prefixed = new ArrayList<>();
        for (String item : majorlist){
            if(item.startsWith(pre))
                prefixed.add(item);
        }
        return prefixed;
    }
}
