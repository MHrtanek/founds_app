package com.example.project;

import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    private RecyclerView expensesRecyclerView;
    private Button btnAdd, btnViewMore, btnDaily, btnWeekly, btnMonthly;
    private BarChart expenseChart;
    private List<Expense> allExpenses = new ArrayList<>(); // Store all expenses
    private List<Expense> recentExpenses = new ArrayList<>(); // Show only recent 3
    private ExpenseAdapter adapter; // Deklarácia adapteru
    private String currentFilter = "Deň"; // Default filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right + 16, systemBars.bottom);
            return insets;
        });

        TextView dateTextView = findViewById(R.id.dateTextView);
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnViewMore = findViewById(R.id.btnViewMore);
        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);
        expenseChart = findViewById(R.id.expenseChart);

        // Nastav dátum
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        // INICIALIZUJ ADAPTER A RECYCLERVIEW - PRIDAJ TOTO
        adapter = new ExpenseAdapter(recentExpenses);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expensesRecyclerView.setAdapter(adapter);

        // Setup chart
        setupChart();

        // Setup filter buttons
        setupFilterButtons();

        // Add some sample data for testing if no expenses exist
        if (allExpenses.isEmpty()) {
            addSampleData();
        }

        // BTN functionality adding new expense.
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1); // Zmena na startActivityForResult
        });

        // BTN view more
        btnViewMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllExpensesActivity.class);
            intent.putExtra("ALL_EXPENSES", allExpenses.toArray(new Expense[0]));
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

            // Pridať do všetkých výdavkov na začiatok
            allExpenses.add(0, newExpense);
            
            // Pridať do recent výdavkov na začiatok
            recentExpenses.add(0, newExpense);

            // Obmedziť na maximálne 3 výdavky v hlavnom zozname
            if (recentExpenses.size() > 3) {
                recentExpenses = recentExpenses.subList(0, 3);
            }

            adapter.notifyDataSetChanged(); // Teraz už adapter existuje
            checkViewMoreButton();
            updateChart(); // Update chart when new expense is added
        }
    }

    private void checkViewMoreButton() {
        if (allExpenses.size() >= 3) {
            btnViewMore.setVisibility(View.VISIBLE);
        }
    }

    private void setupChart() {
        try {
            expenseChart.getDescription().setEnabled(false);
            expenseChart.setDrawGridBackground(false);
            expenseChart.setDrawBarShadow(false);
            expenseChart.setDrawValueAboveBar(true);
            expenseChart.setPinchZoom(false);
            expenseChart.setScaleEnabled(false);
            expenseChart.getLegend().setEnabled(false);

            // Set chart background to white/light
            expenseChart.setBackgroundColor(Color.WHITE);
            expenseChart.setGridBackgroundColor(Color.LTGRAY);

            // Setup X-axis
            XAxis xAxis = expenseChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(7);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setTextSize(12f);
            xAxis.setAxisLineColor(Color.BLACK);

            // Setup Y-axis
            expenseChart.getAxisLeft().setDrawGridLines(true);
            expenseChart.getAxisLeft().setGridColor(Color.LTGRAY);
            expenseChart.getAxisLeft().setTextColor(Color.BLACK);
            expenseChart.getAxisLeft().setTextSize(12f);
            expenseChart.getAxisLeft().setAxisLineColor(Color.BLACK);
            expenseChart.getAxisRight().setEnabled(false);

            // Make chart visible
            expenseChart.setVisibility(View.VISIBLE);

            updateChart();
        } catch (Exception e) {
            // If chart setup fails, make it invisible
            expenseChart.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void setupFilterButtons() {
        // Set initial button state
        updateFilterButtonStates();

        btnDaily.setOnClickListener(v -> {
            currentFilter = "Deň";
            updateFilterButtonStates();
            updateChart();
        });

        btnWeekly.setOnClickListener(v -> {
            currentFilter = "Týždeň";
            updateFilterButtonStates();
            updateChart();
        });

        btnMonthly.setOnClickListener(v -> {
            currentFilter = "Mesiac";
            updateFilterButtonStates();
            updateChart();
        });
    }

    private void updateFilterButtonStates() {
        // Reset all buttons to default style
        btnDaily.setBackgroundColor(Color.TRANSPARENT);
        btnWeekly.setBackgroundColor(Color.TRANSPARENT);
        btnMonthly.setBackgroundColor(Color.TRANSPARENT);

        // Highlight selected button
        switch (currentFilter) {
            case "Deň":
                btnDaily.setBackgroundColor(Color.parseColor("#E3F2FD"));
                break;
            case "Týždeň":
                btnWeekly.setBackgroundColor(Color.parseColor("#E3F2FD"));
                break;
            case "Mesiac":
                btnMonthly.setBackgroundColor(Color.parseColor("#E3F2FD"));
                break;
        }
    }

    private void updateChart() {
        try {
            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            Map<String, Float> aggregatedData = getAggregatedData();

            int index = 0;
            for (Map.Entry<String, Float> entry : aggregatedData.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue()));
                labels.add(entry.getKey());
                index++;
            }

            if (entries.isEmpty()) {
                // Add a dummy entry to show something
                entries.add(new BarEntry(0, 0));
                labels.add("Žiadne dáta");
            }

            BarDataSet dataSet = new BarDataSet(entries, "Výdavky");
            dataSet.setColor(Color.parseColor("#4CAF50")); // Green color for better visibility
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.BLACK);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.8f);
            barData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.0f€", value);
                }
            });

            expenseChart.setData(barData);
            expenseChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            expenseChart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Float> getAggregatedData() {
        Map<String, Float> aggregatedData = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();

        for (Expense expense : allExpenses) {
            String key = getKeyForExpense(expense, currentDate);
            if (key != null) {
                aggregatedData.put(key, aggregatedData.getOrDefault(key, 0f) + (float) expense.getAmount());
            }
        }

        // Fill missing periods with 0 values
        fillMissingPeriods(aggregatedData, currentDate);

        return aggregatedData;
    }

    private String getKeyForExpense(Expense expense, Date currentDate) {
        Calendar expenseCalendar = Calendar.getInstance();
        expenseCalendar.setTime(expense.getDate());

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);

        switch (currentFilter) {
            case "Deň":
                // Show last 7 days
                if (isWithinDays(expense.getDate(), currentDate, 7)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    return sdf.format(expense.getDate());
                }
                break;
            case "Týždeň":
                // Show last 4 weeks
                if (isWithinWeeks(expense.getDate(), currentDate, 4)) {
                    Calendar weekCalendar = Calendar.getInstance();
                    weekCalendar.setTime(expense.getDate());
                    weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    return "Týždeň " + sdf.format(weekCalendar.getTime());
                }
                break;
            case "Mesiac":
                // Show last 6 months
                if (isWithinMonths(expense.getDate(), currentDate, 6)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                    return sdf.format(expense.getDate());
                }
                break;
        }
        return null;
    }

    private void fillMissingPeriods(Map<String, Float> data, Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        switch (currentFilter) {
            case "Deň":
                for (int i = 6; i >= 0; i--) {
                    Calendar dayCalendar = Calendar.getInstance();
                    dayCalendar.setTime(currentDate);
                    dayCalendar.add(Calendar.DAY_OF_MONTH, -i);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    String key = sdf.format(dayCalendar.getTime());
                    data.putIfAbsent(key, 0f);
                }
                break;
            case "Týždeň":
                for (int i = 3; i >= 0; i--) {
                    Calendar weekCalendar = Calendar.getInstance();
                    weekCalendar.setTime(currentDate);
                    weekCalendar.add(Calendar.WEEK_OF_YEAR, -i);
                    weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    String key = "Týždeň " + sdf.format(weekCalendar.getTime());
                    data.putIfAbsent(key, 0f);
                }
                break;
            case "Mesiac":
                for (int i = 5; i >= 0; i--) {
                    Calendar monthCalendar = Calendar.getInstance();
                    monthCalendar.setTime(currentDate);
                    monthCalendar.add(Calendar.MONTH, -i);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                    String key = sdf.format(monthCalendar.getTime());
                    data.putIfAbsent(key, 0f);
                }
                break;
        }
    }

    private boolean isWithinDays(Date expenseDate, Date currentDate, int days) {
        Calendar expenseCalendar = Calendar.getInstance();
        expenseCalendar.setTime(expenseDate);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        currentCalendar.add(Calendar.DAY_OF_MONTH, -days);

        return expenseDate.after(currentCalendar.getTime()) || expenseDate.equals(currentCalendar.getTime());
    }

    private boolean isWithinWeeks(Date expenseDate, Date currentDate, int weeks) {
        Calendar expenseCalendar = Calendar.getInstance();
        expenseCalendar.setTime(expenseDate);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        currentCalendar.add(Calendar.WEEK_OF_YEAR, -weeks);

        return expenseDate.after(currentCalendar.getTime()) || expenseDate.equals(currentCalendar.getTime());
    }

    private boolean isWithinMonths(Date expenseDate, Date currentDate, int months) {
        Calendar expenseCalendar = Calendar.getInstance();
        expenseCalendar.setTime(expenseDate);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        currentCalendar.add(Calendar.MONTH, -months);

        return expenseDate.after(currentCalendar.getTime()) || expenseDate.equals(currentCalendar.getTime());
    }

    private void addSampleData() {
        // Add some sample expenses for testing the chart
        Calendar calendar = Calendar.getInstance();
        
        // Today
        Expense expense1 = new Expense(25.50, "Test expense today", "Potraviny", new Date());
        allExpenses.add(expense1);
        recentExpenses.add(expense1);
        
        // Yesterday
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Expense expense2 = new Expense(15.75, "Test expense yesterday", "Doprava", calendar.getTime());
        allExpenses.add(expense2);
        recentExpenses.add(expense2);
        
        // 2 days ago
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Expense expense3 = new Expense(45.00, "Test expense 2 days ago", "Bývanie", calendar.getTime());
        allExpenses.add(expense3);
        recentExpenses.add(expense3);
        
        // 3 days ago
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Expense expense4 = new Expense(30.25, "Test expense 3 days ago", "Zábava", calendar.getTime());
        allExpenses.add(expense4);
        
        // Update UI
        adapter.notifyDataSetChanged();
        updateChart();
        checkViewMoreButton();
    }
}