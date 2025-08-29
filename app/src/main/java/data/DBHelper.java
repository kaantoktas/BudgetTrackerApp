package com.example.budget.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "budget.db";
    public static final int DB_VERSION = 1;

    public static final String TBL_TX = "transactions";
    public static final String TBL_BUDGET = "budgets";

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TBL_TX + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT NOT NULL," +               // 'income' or 'expense'
                "category TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "note TEXT," +
                "date INTEGER NOT NULL" +             // epoch millis (midnight)
                ");");

        db.execSQL("CREATE TABLE " + TBL_BUDGET + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "month TEXT UNIQUE NOT NULL," +       // 'YYYY-MM'
                "limit REAL NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_TX);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_BUDGET);
        onCreate(db);
    }
}