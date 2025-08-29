package com.example.budget.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budget.R;
import com.example.budget.model.Transaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {

    private final List<Transaction> items = new ArrayList<>();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("tr","TR"));

    public void submit(List<Transaction> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Transaction t = items.get(pos);
        h.tvCategory.setText(t.category);
        h.tvNote.setText(t.note == null ? "" : t.note);
        h.tvDate.setText(t.formattedDate());
        String amountStr = currency.format(t.amount);
        h.tvAmount.setText(amountStr);
        h.tvSign.setText(t.type.equals("income") ? "+" : "-");
        h.tvSign.setTextColor(t.type.equals("income") ?
                h.itemView.getResources().getColor(R.color.accent) :
                h.itemView.getResources().getColor(R.color.danger));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvSign, tvCategory, tvNote, tvAmount, tvDate;
        VH(@NonNull View itemView) {
            super(itemView);
            tvSign = itemView.findViewById(R.id.tvSign);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}