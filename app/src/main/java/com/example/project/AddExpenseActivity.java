package com.example.project;



import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etDescription;
    private Spinner spinnerCategory;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Nájdeme elementy
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Nastavíme kategórie pre Spinner
        setupCategorySpinner();

        // Nastavíme klik listenery
        btnSave.setOnClickListener(v -> saveExpense());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        List<String> categories = Arrays.asList(
                "Potraviny", "Bývanie", "Doprava", "Zábava",
                "Oblečenie", "Zdravie", "Jedlo", "Iné"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveExpense() {
        String amountText = etAmount.getText().toString();
        String description = etDescription.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (amountText.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Prosim vyplnte všetky polia ", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);
        Date currentDate = new Date();

        // Vytvoriť nový výdavok
        Expense newExpense = new Expense(amount, description, category, currentDate);

        // Poslať výdavok späť do MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("AMOUNT", amount);
        resultIntent.putExtra("DESCRIPTION", description);
        resultIntent.putExtra("CATEGORY", category);
        resultIntent.putExtra("DATE", currentDate.getTime()); // uložiť ako timestamp

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}