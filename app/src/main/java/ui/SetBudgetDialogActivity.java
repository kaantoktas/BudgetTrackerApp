package com.example.budget.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budget.R;
import com.example.budget.data.Repository;
import com.example.budget.databinding.ActivitySetBudgetDialogBinding;

import java.util.Calendar;

public class SetBudgetDialogActivity extends AppCompatActivity {

    private ActivitySetBudgetDialogBinding b;
    private Repository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySetBudgetDialogBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        repo = new Repository(this);

        b.btnBudgetCancel.setOnClickListener(v -> finish());
        b.btnBudgetSave.setOnClickListener(v -> {
            String vStr = String.valueOf(b.etBudget.getText()).trim();
            if (TextUtils.isEmpty(vStr)) {
                Toast.makeText(this, "Bütçe değeri girin.", Toast.LENGTH_SHORT).show();
                return;
            }
            double limit;
            try {
                limit = Double.parseDouble(vStr.replace(',', '.'));
            } catch (Exception e) {
                Toast.makeText(this, "Geçersiz değer.", Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar cal = Calendar.getInstance();
            String monthKey = String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);
            repo.upsertBudget(monthKey, limit);
            Toast.makeText(this, "Bütçe ayarlandı.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }
}
