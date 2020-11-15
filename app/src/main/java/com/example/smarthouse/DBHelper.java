package com.example.smarthouse;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "myDB";
    public static final String NUMBERS_TABLE = "numbers";
    public static final String FAMILY_TABLE = "family";
    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями
        db.execSQL("create table " + NUMBERS_TABLE + "("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "phone text,"
                + "password text,"
                + "adminflag boolean" + ");");//создание или инициализация таблицы с розетками

        db.execSQL("create table " + FAMILY_TABLE + "("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "phone text,"
                + "socket text" + ");");//создание или инициализация таблицы с членами семьи
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tableName");
        db.execSQL("DROP TABLE IF EXISTS family");
        onCreate(db);
    }
}
