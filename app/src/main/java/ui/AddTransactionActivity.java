package com.example.budget.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budget.R;
import com.example.budget.data.Repository;
import com.example.budget.model.Transaction;
import com.example.budget.databinding.ActivityAddTransactionBinding;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private ActivityAddTransactionBinding b;
    private Repository repo;
    private long chosenDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        repo = new Repository(this);

        // Type options
        List<String> types = Arrays.asList("Gelir", "Gider");
        b.spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, types));

        // Default categories (will be replaced when type changes)
        setCategoriesForType("Gider");

        b.spType.setOnItemClickListener((parent, view, position, id) -> {
            String t = (String) parent.getItemAtPosition(position);
            setCategoriesForType(t);
        });

        Calendar cal = Calendar.getInstance();
        chosenDateMillis = midnight(cal).getTimeInMillis();
        b.etDate.setText(String.format(new Locale("tr","TR"), "%02d.%02d.%04d",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR)));
        b.etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                Calendar cc = Calendar.getInstance();
                cc.set(Calendar.YEAR, y);
                cc.set(Calendar.MONTH, m);
                cc.set(Calendar.DAY_OF_MONTH, d);
                cc.set(Calendar.HOUR_OF_DAY, 0);
                cc.set(Calendar.MINUTE, 0);
                cc.set(Calendar.SECOND, 0);
                chosenDateMillis = cc.getTimeInMillis();
                b.etDate.setText(String.format(new Locale("tr","TR"), "%02d.%02d.%04d", d, m+1, y));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        b.btnCancel.setOnClickListener(v -> finish());
        b.btnSave.setOnClickListener(v -> save());
    }

    private void setCategoriesForType(String typeDisplay) {
        boolean isIncome = typeDisplay.equalsIgnoreCase("Gelir");
        List<String> cats = isIncome
                ? Arrays.asList("Maaş","Serbest İş","Yatırım","Hediye","Diğer")
                : Arrays.asList("Yemek","Ulaşım","Kira","Faturalar","Eğlence","Sağlık","Market","Giyim","Diğer");
        b.spCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cats));
    }

    private void save() {
        String amountStr = String.valueOf(b.etAmount.getText()).trim();
        String note = String.valueOf(b.etNote.getText()).trim();
        String typeDisplay = String.valueOf(b.spType.getText()).trim();
        String category = String.valueOf(b.spCategory.getText()).trim();

        if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(typeDisplay) || TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Lütfen tüm zorunlu alanları doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.replace(',', '.'));
        } catch (Exception e) {
            Toast.makeText(this, "Geçersiz tutar.", Toast.LENGTH_SHORT).show();
            return;
        }

        Transaction t = new Transaction();
        t.amount = amount;
        t.note = note;
        t.type = typeDisplay.equalsIgnoreCase("Gelir") ? "income" : "expense";
        t.category = category;
        t.date = chosenDateMillis;

        repo.insertTransaction(t);
        Toast.makeText(this, "Kaydedildi.", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private Calendar midnight(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
}