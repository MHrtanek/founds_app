package com.example.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllExpensesActivity extends AppCompatActivity {

    private RecyclerView allExpensesRecyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> allExpenses = new ArrayList<>();
    private TextView totalExpensesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses);

        // Initialize views
        allExpensesRecyclerView = findViewById(R.id.allExpensesRecyclerView);
        totalExpensesTextView = findViewById(R.id.totalExpensesTextView);
        Button btnBack = findViewById(R.id.btnBack);

        // Get all expenses from intent
        Expense[] expensesArray = (Expense[]) getIntent().getSerializableExtra("ALL_EXPENSES");
        if (expensesArray != null) {
            allExpenses = new ArrayList<>(Arrays.asList(expensesArray));
        }

        // Setup RecyclerView
        adapter = new ExpenseAdapter(allExpenses);
        allExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allExpensesRecyclerView.setAdapter(adapter);

        // Calculate and display total
        updateTotalExpenses();

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateTotalExpenses() {
        double total = 0;
        for (Expense expense : allExpenses) {
            total += expense.getAmount();
        }
        totalExpensesTextView.setText(String.format("Celkový súčet: %.2f €", total));
    }
}