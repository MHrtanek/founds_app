package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    private RecyclerView expensesRecyclerView;
    private Button btnAdd, btnViewMore;
    private List<Expense> expenses = new ArrayList<>();
    private ExpenseAdapter adapter; // Deklarácia adapteru

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView dateTextView = findViewById(R.id.dateTextView);
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnViewMore = findViewById(R.id.btnViewMore);

        // Nastav dátum
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        // INICIALIZUJ ADAPTER A RECYCLERVIEW - PRIDAJ TOTO
        adapter = new ExpenseAdapter(expenses);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expensesRecyclerView.setAdapter(adapter);

        // BTN functionality adding new expense.
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1); // Zmena na startActivityForResult
        });

        // BTN view more
        btnViewMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllExpensesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            double amount = data.getDoubleExtra("AMOUNT", 0);
            String description = data.getStringExtra("DESCRIPTION");
            String category = data.getStringExtra("CATEGORY");
            long dateTimestamp = data.getLongExtra("DATE", System.currentTimeMillis());

            Date date = new Date(dateTimestamp);
            Expense newExpense = new Expense(amount, description, category, date);

            // Pridať do zoznamu na začiatok
            expenses.add(0, newExpense);

            // Obmedziť na maximálne 3 výdavky v hlavnom zozname
            if (expenses.size() > 3) {
                expenses = expenses.subList(0, 3);
            }

            adapter.notifyDataSetChanged(); // Teraz už adapter existuje
            checkViewMoreButton();
        }
    }

    private void checkViewMoreButton() {
        if (expenses.size() >= 3) {
            btnViewMore.setVisibility(View.VISIBLE);
        }
    }
}