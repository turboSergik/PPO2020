package com.sergey.codeeditorPPO2020.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Codes";

    public static final String TABLE_FILES= "Files";
    // public static final String TABLE_TASKS = "tasksDB";
    // public static final String TABLE_PASSWORD = "passwordDB";

    public static final String KEY_ID = "_id";

    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_TEXT = "data";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FILES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FILE_NAME + " TEXT," + KEY_TEXT + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        onCreate(db);
    }
}
