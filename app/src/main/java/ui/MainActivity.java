package com.example.budget.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budget.R;
import com.example.budget.data.Repository;
import com.example.budget.data.Repository.CategorySum;
import com.example.budget.model.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Repository repo;
    private RecyclerView rv;
    private TransactionAdapter adapter;
    private TextView tvBudgetStatus, tvMonth;
    private ProgressBar progress;
    private PieChart pieChart;
    private final Locale tr = new Locale("tr","TR");
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(tr);
    private int year, month; // month=0-11

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        repo = new Repository(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = findViewById(R.id.rvTransactions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();
        rv.setAdapter(adapter);

        tvBudgetStatus = findViewById(R.id.tvBudgetStatus);
        tvMonth = findViewById(R.id.tvMonth);
        progress = findViewById(R.id.progressBudget);
        pieChart = findViewById(R.id.pieChart);
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        MaterialButton btnSetBudget = findViewById(R.id.btnSetBudget);

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);

        tvMonth.setText(new SimpleDateFormat("MMMM yyyy", tr).format(cal.getTime()));

        fab.setOnClickListener(v -> startActivityForResult(new Intent(this, AddTransactionActivity.class), 1001));
        btnSetBudget.setOnClickListener(v -> startActivityForResult(new Intent(this, SetBudgetDialogActivity.class), 2001));

        refreshUI();
    }

    private void refreshUI() {
        // List
        List<Transaction> list = repo.listTransactionsForMonth(year, month);
        adapter.submit(list);

        // Sums
        double income = repo.sumByTypeForMonth(year, month, "income");
        double expense = repo.sumByTypeForMonth(year, month, "expense");

        // Budget
        String monthKey = String.format("%04d-%02d", year, month + 1);
        double limit = repo.getBudgetLimit(monthKey);

        tvBudgetStatus.setText("Gelir: " + currency.format(income) +
                "   |   Gider: " + currency.format(expense) +
                (limit > 0 ? ("   |   Bütçe: " + currency.format(limit)) : ""));

        if (limit > 0) {
            int pct = (int) Math.min(100, Math.round((expense / limit) * 100.0));
            progress.setProgress(pct);
            progress.setProgressTintList(getColorStateList(expense > limit ? R.color.danger : R.color.accent));
        } else {
            progress.setProgress(0);
        }

        // Chart
        List<CategorySum> catSums = repo.sumExpensesByCategoryForMonth(year, month);
        List<PieEntry> entries = new ArrayList<>();
        for (CategorySum cs : catSums) {
            if (cs.sum > 0) entries.add(new PieEntry((float) cs.sum, cs.category));
        }
        PieDataSet set = new PieDataSet(entries, "");
        set.setSliceSpace(2f);
        set.setValueTextSize(12f);
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        pieChart.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshUI();
    }
}
