package com.androider.legacy.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.database.DatabaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.MalformedInputException;
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
    public ArrayList<String> majorList;
    public ArrayList<String> getMajors(){
        if(majors.equals(Constants.emptyString))
            return null;
        majorList = new ArrayList<>();
        Cursor cursor = DatabaseHelper.db.query("major", new String[]{"name"}, "id in (" + majors + ")", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            majorList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return majorList;
    }

    public static ArrayList<School> regional(int region){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from school where region = ?;", new String[]{Constants.emptyString + region});
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

    public static int proviceId(String province){
        Cursor cursor = DatabaseHelper.db.rawQuery("select id from region where name = ?;", new String[]{province});
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    public static int schoolId(String school){
        Cursor cursor = DatabaseHelper.db.rawQuery("select id from school where name = ?;", new String[]{school});
        int result = -1;
        cursor .moveToFirst();
        if(!cursor.isAfterLast()){
            result = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return result;
        }
        cursor.close();
        return result;
    }

    public static int majorId(String major){
        Cursor cursor = DatabaseHelper.db.rawQuery("select id from major where name = ?;", new String[]{major});
        int result = -1;
        cursor .moveToFirst();
        if(!cursor.isAfterLast()){
            result = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return result;
        }else{
            cursor.close();
            cursor = DatabaseHelper.db.rawQuery("select id form major where name like ?;", new String[]{"%" + major + "%"});
            cursor.moveToFirst();
            if(!cursor.isAfterLast()){
                result = cursor.getInt(cursor.getColumnIndex("id"));
                cursor.close();
                return result;
            }
            cursor.close();
        }
        return result;
    }

    public static ArrayList<String> regional(String province){
        ArrayList<School> schools = regional(proviceId(province));
        ArrayList<String> result = new ArrayList<>();
        maybeUsed = new HashMap<>();
        for(School item : schools){
            maybeUsed.put(item.name, item);
            result.add(item.name);
        }
        return result;
    }

    public static ArrayList<String> prefixed(String pre){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from school where name like ?;", new String[]{pre + "%"});
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
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from School;", null);
        int judger = cursor.getCount();
        if(judger != 0){
            cursor.close();
            return;
        }
        AssetManager manager = context.getAssets();
        DatabaseHelper.db.beginTransaction();
        try {
            InputStream in = manager.open("majorbase.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String str;
            ContentValues cv;
            while ((str = reader.readLine()) != null){
                String[] majorFields = str.split(";");
                cv = new ContentValues();
                cv.put("id", Integer.parseInt(majorFields[0]));
                cv.put("name", majorFields[1]);

                DatabaseHelper.db.insert("major", null, cv);
            }
            reader.close();
            in.close();
            in = manager.open("regionbase.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            while ((str = reader.readLine()) != null){
                String[] schoolFields = str.split(";");
                cv = new ContentValues();
                cv.put("name", schoolFields[1]);
                cv.put("id", Integer.parseInt(schoolFields[0]));
                DatabaseHelper.db.insert("region", null, cv);
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
                cv.put("major", schoolFields[2]);
                DatabaseHelper.db.insert("school", null, cv);
            }
            reader.close();
            in.close();
            DatabaseHelper.db.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            DatabaseHelper.db.endTransaction();
        }
    }

    public ArrayList<String> prefixedMajors(String pre){
        ArrayList<String> prefixed = new ArrayList<>();
        for (String item : majorList){
            if(item.startsWith(pre))
                prefixed.add(item);
        }
        return prefixed;
    }

}
