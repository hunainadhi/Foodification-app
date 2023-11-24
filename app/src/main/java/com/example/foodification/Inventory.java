package com.example.foodification;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Inventory extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private RecyclerView ingredientList;
    private String email, safeEmail;
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = preferences.getString("global_variable_key", "default_value");

        FirebaseApp.initializeApp(this);

        safeEmail = email.replace('.', ',')
                .replace('#', '-')
                .replace('$', '+')
                .replace('[', '(')
                .replace(']', ')');
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(safeEmail)
                .child("ingredients");

        FloatingActionButton addIngredientButton = findViewById(R.id.addIngredientButton);
        ingredientList = findViewById(R.id.ingredientList);
        Button backbtn = findViewById(R.id.backButton);

        adapter = new IngredientAdapter(this, new ArrayList<Ingredient>(), new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(final int position) {
                // Handle the delete action here
                showDeleteConfirmationDialog(position);
            }

            @Override
            public void onModifyClick(int position) {
                // Handle the modify action here
                Ingredient ingredient = adapter.getIngredient(position);
                if (ingredient != null) {
                    showModifyDialog(ingredient);
                }
            }
        });

        ingredientList.setAdapter(adapter);
        ingredientList.setLayoutManager(new LinearLayoutManager(this));

        updateInventory();

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIngredientDialog();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.setIngredients(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Ingredient");
        builder.setMessage("Are you sure you want to delete this ingredient?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteIngredient(position);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cancel the deletion
            }
        });

        builder.show();
    }

    private void deleteIngredient(int position) {
        Ingredient ingredient = adapter.getIngredient(position);
        if (ingredient != null) {
            databaseReference.child(ingredient.getId()).removeValue();
            Toast.makeText(this, "Ingredient deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showModifyDialog(final Ingredient ingredient) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_ingredient);

        final EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialog.findViewById(R.id.quantityEditText);
        final Spinner unitSpinner = dialog.findViewById(R.id.unitSpinner);
        Button modifyButton = dialog.findViewById(R.id.addButton);

        nameEditText.setText(ingredient.getName());
        quantityEditText.setText(String.valueOf(ingredient.getQuantity()));

        String[] units = new String[] {"Select Unit", "kg", "g", "lbs", "oz", "ml", "l", "cup"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerAdapter);

        int spinnerPosition = spinnerAdapter.getPosition(ingredient.getUnit());
        unitSpinner.setSelection(spinnerPosition);

        modifyButton.setText("Modify");

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();
                String unit = unitSpinner.getSelectedItem().toString().trim();

                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unit.equalsIgnoreCase("Select Unit")) {
                    double quantity = Double.parseDouble(quantityStr);

                    ingredient.setName(name);
                    ingredient.setQuantity(quantity);
                    ingredient.setUnit(unit);

                    databaseReference.child(ingredient.getId()).setValue(ingredient);

                    Toast.makeText(Inventory.this, "Ingredient modified!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(Inventory.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showAddIngredientDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_ingredient);

        final EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialog.findViewById(R.id.quantityEditText);
        final Spinner unitSpinner = dialog.findViewById(R.id.unitSpinner);
        Button addButton = dialog.findViewById(R.id.addButton);

        String[] units = new String[] {"Select Unit", "Kg", "g", "Lbs", "Oz", "Ml", "L", "Cup"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();
                String unit = unitSpinner.getSelectedItem().toString().trim();

                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unit.equalsIgnoreCase("Select Unit")) {
                    double quantity = Double.parseDouble(quantityStr);

                    String ingredientId = databaseReference.push().getKey();
                    Ingredient ingredient = new Ingredient(ingredientId, name, quantity, unit);

                    if (ingredientId != null) {
                        databaseReference.child(ingredientId).setValue(ingredient);
                        Toast.makeText(Inventory.this, "Ingredient added!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(Inventory.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void updateInventory() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing ingredients
                adapter.clearIngredients();
                // Add the ingredients from the database
                adapter.setIngredients(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors

            }
        });
    }
}
