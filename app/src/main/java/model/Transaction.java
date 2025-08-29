package com.example.budget.model;

import android.database.Cursor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    public long id;
    public String type;     // "income" or "expense"
    public String category;
    public double amount;
    public String note;
    public long date;       // epoch millis

    public static Transaction fromCursor(Cursor c) {
        Transaction t = new Transaction();
        t.id = c.getLong(c.getColumnIndexOrThrow("id"));
        t.type = c.getString(c.getColumnIndexOrThrow("type"));
        t.category = c.getString(c.getColumnIndexOrThrow("category"));
        t.amount = c.getDouble(c.getColumnIndexOrThrow("amount"));
        t.note = c.getString(c.getColumnIndexOrThrow("note"));
        t.date = c.getLong(c.getColumnIndexOrThrow("date"));
        return t;
    }

    public String formattedDate() {
        return new SimpleDateFormat("dd.MM.yyyy", new Locale("tr", "TR")).format(new Date(date));
    }
}
