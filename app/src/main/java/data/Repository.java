package com.example.budget.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.budget.model.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Repository {
    private final DBHelper helper;

    public Repository(Context ctx) {
        helper = new DBHelper(ctx);
    }

    public long insertTransaction(Transaction t) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", t.type);
        cv.put("category", t.category);
        cv.put("amount", t.amount);
        cv.put("note", t.note);
        cv.put("date", t.date);
        return db.insert(DBHelper.TBL_TX, null, cv);
    }

    public List<Transaction> listTransactionsForMonth(int year, int month) {
        // month: 0-11 expected
        long start = monthStartMillis(year, month);
        long end = monthEndMillis(year, month);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TBL_TX, null, "date BETWEEN ? AND ?",
                new String[]{String.valueOf(start), String.valueOf(end)}, null, null, "date DESC");
        List<Transaction> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(Transaction.fromCursor(c));
        }
        c.close();
        return list;
    }

    public double sumByTypeForMonth(int year, int month, String type) {
        long start = monthStartMillis(year, month);
        long end = monthEndMillis(year, month);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(amount),0) FROM " + DBHelper.TBL_TX +
                        " WHERE type=? AND date BETWEEN ? AND ?",
                new String[]{type, String.valueOf(start), String.valueOf(end)});
        double sum = 0;
        if (c.moveToFirst()) sum = c.getDouble(0);
        c.close();
        return sum;
    }

    public List<CategorySum> sumExpensesByCategoryForMonth(int year, int month) {
        long start = monthStartMillis(year, month);
        long end = monthEndMillis(year, month);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT category, IFNULL(SUM(amount),0) FROM " + DBHelper.TBL_TX +
                        " WHERE type='expense' AND date BETWEEN ? AND ? GROUP BY category",
                new String[]{String.valueOf(start), String.valueOf(end)});
        List<CategorySum> result = new ArrayList<>();
        while (c.moveToNext()) {
            result.add(new CategorySum(c.getString(0), c.getDouble(1)));
        }
        c.close();
        return result;
    }

    public void upsertBudget(String month, double limit) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("limit", limit);
        long id = db.insertWithOnConflict(DBHelper.TBL_BUDGET, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(DBHelper.TBL_BUDGET, cv, "month=?", new String[]{month});
        }
    }

    public double getBudgetLimit(String month) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TBL_BUDGET, new String[]{"limit"}, "month=?",
                new String[]{month}, null, null, null);
        double limit = 0;
        if (c.moveToFirst()) limit = c.getDouble(0);
        c.close();
        return limit;
    }

    private static long monthStartMillis(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTimeInMillis();
    }

    private static long monthEndMillis(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }

    public static class CategorySum {
        public final String category;
        public final double sum;
        public CategorySum(String category, double sum) {
            this.category = category; this.sum = sum;
        }
    }
}
