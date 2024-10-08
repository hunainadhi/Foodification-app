package com.example.foodification;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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

public class InventoryFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView ingredientList;
    private String email, safeEmail;
    private IngredientAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_inventory, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String email = preferences.getString("global_variable_key", "default_value");

        FirebaseApp.initializeApp(requireContext());

        safeEmail = email.replace('.', ',')
                .replace('#', '-')
                .replace('$', '+')
                .replace('[', '(')
                .replace(']', ')');
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(safeEmail)
                .child("ingredients");

        FloatingActionButton addIngredientButton = view.findViewById(R.id.addIngredientButton);
        ingredientList = view.findViewById(R.id.ingredientList);


        adapter = new IngredientAdapter(requireContext(), new ArrayList<Ingredient>(), new IngredientAdapter.OnItemClickListener() {
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
        ingredientList.setLayoutManager(new LinearLayoutManager(requireContext()));

        updateInventory();

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIngredientDialog();
            }
        });

//        addIngredientButton.setOnClickListener(view -> showAddIngredientDialog());
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.setIngredients(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
            databaseReference.child(ingredient.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Ingredient deleted!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error deleting ingredient", Toast.LENGTH_SHORT).show());
        }
    }

    private void showModifyDialog(final Ingredient ingredient) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_ingredient);

        final EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialog.findViewById(R.id.quantityEditText);
        final Spinner unitSpinner = dialog.findViewById(R.id.unitSpinner);
        Button modifyButton = dialog.findViewById(R.id.addButton);

        nameEditText.setText(ingredient.getName());
        quantityEditText.setText(String.valueOf(ingredient.getQuantity()));

        String[] units = new String[]{"Select Unit", "kg", "g", "lbs", "oz", "ml", "l", "cup"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, units);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerAdapter);

        int spinnerPosition = spinnerAdapter.getPosition(ingredient.getUnit());
        unitSpinner.setSelection(spinnerPosition);

        modifyButton.setText("Modify");

        modifyButton.setOnClickListener(view -> {
                String name = nameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();
                String unit = unitSpinner.getSelectedItem().toString().trim();

                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unit.equalsIgnoreCase("Select Unit")) {
                    double quantity = Double.parseDouble(quantityStr);

                    ingredient.setName(name);
                    ingredient.setQuantity(quantity);
                    ingredient.setUnit(unit);

                    databaseReference.child(ingredient.getId()).setValue(ingredient)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), "Ingredient modified!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error modifying ingredient", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            });

        dialog.show();
    }

    private void showAddIngredientDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_ingredient);

        final EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialog.findViewById(R.id.quantityEditText);
        final Spinner unitSpinner = dialog.findViewById(R.id.unitSpinner);
        Button addButton = dialog.findViewById(R.id.addButton);

        String[] units = new String[]{"Select Unit", "Kg", "g", "Lbs", "Oz", "Ml", "L", "Cup"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, units);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerAdapter);

        addButton.setOnClickListener(view -> {
                String name = nameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();
                String unit = unitSpinner.getSelectedItem().toString().trim();

                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unit.equalsIgnoreCase("Select Unit")) {
                    double quantity = Double.parseDouble(quantityStr);
                    String ingredientId = databaseReference.push().getKey();
                    Ingredient ingredient = new Ingredient(ingredientId, name, quantity, unit);

                    if (ingredientId != null) {
                        databaseReference.child(ingredientId).setValue(ingredient)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Ingredient added!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error adding ingredient", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "Error updating Inventory: " + error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }
}

