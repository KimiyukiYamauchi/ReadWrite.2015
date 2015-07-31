package com.example.yamauchi.readwriteyo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yamauchi on 2015/07/31.
 */
public class DBHelper extends SQLiteOpenHelper {

    static final String TABLENAME = "TestTable"; // テーブル名

    public DBHelper(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "CREATE TABLE IF NOT EXISTS " + TABLENAME
                + "(id integer primary key autoincrement, data text )";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String s = "DROP TABLE IF EXISTS " + TABLENAME;
        db.execSQL(s);
    }
}
