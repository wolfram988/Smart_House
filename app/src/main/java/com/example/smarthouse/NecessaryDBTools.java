package com.example.smarthouse;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

class NecessaryDBTools {
    private DBHelper dbHelper; //класс помощник для sql
    private SQLiteDatabase db;
    private String TABLE_NAME;
    NecessaryDBTools(DBHelper dbHelper,String TABLE_NAME){
        this.dbHelper = dbHelper;
        this.TABLE_NAME = TABLE_NAME;
        db = dbHelper.getWritableDatabase();
    }

    ArrayList<HashMap<String, String>> putMapstoList(ArrayList<String> names, ArrayList<String> phones){
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        HashMap map;
        if(names.size()==phones.size())
        {
            for (int i = 0; i < names.size(); i++) {
                map = new HashMap();
                map.put("Name",names.get(i));
                map.put("Phone",phones.get(i));
                arrayList.add(map);
            }
        }
        return arrayList;

    }

    ArrayList<String> getFromSQLite(String columnName){
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME,null,null,null,null,null,null);
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int ColIndex = c.getColumnIndex(columnName);

            do {
                // получаем значения по номерам столбцов и пишем все в arraylist
                arrayList.add(c.getString(ColIndex));

            } while (c.moveToNext());
        } else
        c.close();
        return arrayList;
    }
    ArrayList<String> getFamilyMembersFromSQLite(String columnName, String name){
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME,null,null,null,null,null,null);
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int ColIndex = c.getColumnIndex(columnName);
            int nameIndex = c.getColumnIndex("socket");

            do {
                // получаем значения по номерам столбцов и пишем все в arraylist
                if(c.getString(nameIndex).equals(name+"")){
                arrayList.add(c.getString(ColIndex));}
                else {Log.i("NecessaryDBTools", "getFromSQLite: "+c.getString(nameIndex));}

            } while (c.moveToNext());
        } else
            c.close();
        return arrayList;
    }

    void putSocketToSQLite(String name,String phone,String password,int adminFlag){
        ContentValues cv = new ContentValues();
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        cv.put("name",name);
        cv.put("phone",phone);
        cv.put("password",password);
        cv.put("adminflag",adminFlag);
        db.insert(TABLE_NAME, null, cv);

    }
    void putFamilyMemberToSQLite(String name,String phone,String socket){
        ContentValues cv = new ContentValues();
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        cv.put("name",name);
        cv.put("phone",phone);
        cv.put("socket",socket);
        db.insert(TABLE_NAME, null, cv);
    }
    void deleteFromSQLite(String name){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TABLE_NAME, "name ='" + name + "'", null);
    }
    void clearSQLiteTable(String TABLE_NAME) {
        db.delete(TABLE_NAME, null, null);
    }
    void clearFamilyMembers(String TABLE_NAME,String socket){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TABLE_NAME,"socket ='" + socket + "'",null);
    }
    void edit(String valType, String oldVal,String newVal){
         ContentValues contentValues = new ContentValues();
         SQLiteDatabase database = dbHelper.getWritableDatabase();
         contentValues.put("name",newVal);
         database.update(TABLE_NAME,contentValues,valType + " ='" + oldVal + "'",null);
    }
    void editSocketName(String oldSocketName, String newSocketName){
        ContentValues socketCV = new ContentValues();
        ContentValues memberCV = new ContentValues();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        socketCV.put("name",newSocketName);
        memberCV.put("socket",newSocketName);
        database.update("numbers",socketCV,"name"+ " ='"+oldSocketName+"'",null);
        database.update("family",memberCV,"socket"+ "='" + oldSocketName +"'",null);
    }

}
