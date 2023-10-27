package com.example.foodification;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Inventory extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private RecyclerView ingredientList;
    private IngredientAdapter adapter;
    private static final int MENU_DELETE_OPTION = R.id.delete_option;
    private static final int MENU_MODIFY_OPTION = R.id.modify_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        FirebaseApp.initializeApp(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("ingredients");

        Button addIngredientButton = findViewById(R.id.addIngredientButton);
        ingredientList = findViewById(R.id.ingredientList);

        // Initialize RecyclerView and Adapter
        adapter = new IngredientAdapter(this);
        ingredientList.setAdapter(adapter);
        ingredientList.setLayoutManager(new LinearLayoutManager(this));

        updateInventory();
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to add ingredients
                showAddIngredientDialog();
            }
        });

        // Attach a listener to retrieve data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.setIngredients(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        // Register the RecyclerView for a context menu
        registerForContextMenu(ingredientList);

        // Set an item click listener for the RecyclerView
// Set an item click listener for the RecyclerView
        adapter.setOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click
                Ingredient ingredient = adapter.getIngredient(position);
                if (ingredient != null) {
                    // Display details for the clicked ingredient
                    showIngredientDetails(ingredient);
                }
            }
        });
    }

    // Method to show the context menu when an item is long-pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    // Handle the context menu item selection
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int selectedItemPosition = adapter.getSelectedItemPosition();

        if (selectedItemPosition == -1) {
            return super.onContextItemSelected(item);
        }

        Ingredient selectedIngredient = adapter.getIngredient(selectedItemPosition);

        if (item.getItemId() == R.id.delete_option) {
            // Delete the selected ingredient
            deleteIngredient(selectedIngredient);
            return true;
        } else if (item.getItemId() == R.id.modify_option) {
            // Show a modify dialog and update the selected ingredient
            showModifyDialog(selectedIngredient);
            return true;
        }

        return super.onContextItemSelected(item);
    }



    // Method to delete an ingredient from Firebase
    private void deleteIngredient(Ingredient ingredient) {
        if (ingredient != null) {
            databaseReference.child(ingredient.getId()).removeValue();
            Toast.makeText(this, "Ingredient deleted!", Toast.LENGTH_SHORT).show();
            adapter.clearSelectedItem();
        }
    }
    // Method to show a dialog for modifying ingredients
    private void showModifyDialog(final Ingredient ingredient) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_ingredient);

        final EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialog.findViewById(R.id.quantityEditText);
        final EditText unitEditText = dialog.findViewById(R.id.unitEditText);
        Button modifyButton = dialog.findViewById(R.id.addButton);

        nameEditText.setText(ingredient.getName());
        quantityEditText.setText(String.valueOf(ingredient.getQuantity()));
        unitEditText.setText(ingredient.getUnit());

        modifyButton.setText("Modify");

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();
                String unit = unitEditText.getText().toString().trim();

                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty()) {
                    double quantity = Double.parseDouble(quantityStr);

                    // Update the selected ingredient's details
                    ingredient.setName(name);
                    ingredient.setQuantity(quantity);
                    ingredient.setUnit(unit);

                    // Update the ingredient in Firebase
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

    // Method to show the dialog for adding ingredients
    private void showAddIngredientDialog() {
        // Your add ingredient dialog code here
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_ingredient);

        final EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialog.findViewById(R.id.quantityEditText);
        final EditText unitEditText = dialog.findViewById(R.id.unitEditText);
        Button addButton = dialog.findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();
                String unit = unitEditText.getText().toString().trim();

                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty()) {
                    double quantity = Double.parseDouble(quantityStr);

                    // Create a new Ingredient object
                    String ingredientId = databaseReference.push().getKey();
                    Ingredient ingredient = new Ingredient(ingredientId, name, quantity, unit);

                    // Save the ingredient to Firebase
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
    private void showIngredientDetails(Ingredient ingredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingredient Details");

        // Customize the dialog content based on the ingredient
        String details = "Name: " + ingredient.getName() + "\n" +
                "Quantity: " + ingredient.getQuantity() + " " + ingredient.getUnit();

        builder.setMessage(details);

        // Add a button to close the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }




}
