package com.yilegame.googlesdk.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/12/11.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mHelper;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "mlhx_googlepay";//数据库名字
    private static final String TABLE_PURCGASE = "purchase_info";//专辑库

    public static DatabaseHelper getHelper(Context context) {
        if(mHelper == null) {
            mHelper = new DatabaseHelper(context);
        }
        return mHelper;
    }

    private  DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    /**
     * signedData, String signature signedData, String signature
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "
                + TABLE_PURCGASE
                + "(_id integer primary key autoincrement,"
                +  "orderId text,"
                +  "purchaseData text,"
                +  "signature text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURCGASE);
            onCreate(db);
        }
    }

}
