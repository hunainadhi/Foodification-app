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
import android.widget.CheckBox;
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

public class GroceryFragment extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceInv;
    private RecyclerView groceryList;
    private String email, safeEmail;
    private GroceryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_grocery, container, false);

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
                .child("grocery");
        databaseReferenceInv = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(safeEmail)
                .child("ingredients");

        FloatingActionButton addGroceryButton = view.findViewById(R.id.addGroceryButton);

        FloatingActionButton refreshGroceryButton = view.findViewById(R.id.refreshGroceryButton);
        groceryList = view.findViewById(R.id.groceryList);
        Button backbtn = view.findViewById(R.id.backButton);

        adapter = new GroceryAdapter(requireContext(), new ArrayList<Grocery>(), new GroceryAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(final int position) {
                // Handle the delete action here
                showDeleteConfirmationDialog(position);
            }

            @Override
            public void onModifyClick(int position) {
                // Handle the modify action here
                Grocery grocery = adapter.getGrocery(position);
                if (grocery != null) {
                    showModifyDialog(grocery);
                }
            }

            public void showOnCheck(int position, Boolean isChecked){

                Grocery grocery = adapter.getGrocery(position);
                showAddCheckDialog(grocery,isChecked);
            }
        });

        groceryList.setAdapter(adapter);
        groceryList.setLayoutManager(new LinearLayoutManager(requireContext()));

        updateGrocery();

        refreshGroceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });

        addGroceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddGroceryDialog();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.setGrocery(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                // Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        return view;
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Ingredient");
        builder.setMessage("Are you sure you want to delete this grocery item?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteGrocery(position);
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

    private void deleteGrocery(int position) {
        Grocery grocery = adapter.getGrocery(position);
        if (grocery != null) {
            databaseReference.child(grocery.getId()).removeValue();
            Toast.makeText(requireContext(), "Grocery deleted!", Toast.LENGTH_SHORT).show();
        }
    }
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Checked Items");
        builder.setMessage("Are you sure you want to delete all checked grocery items?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCheckedGroceries();
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
    private void deleteCheckedGroceries() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grocery grocery = snapshot.getValue(Grocery.class);
                    if (grocery != null && grocery.getCheck()) {
                        // Delete the grocery item
                        databaseReference.child(grocery.getId()).removeValue();
                    }
                }
                Toast.makeText(requireContext(), "Checked groceries deleted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Toast.makeText(requireContext(), "Error deleting checked groceries", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showModifyDialog(final Grocery grocery) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_ingredient);

        final EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialog.findViewById(R.id.quantityEditText);
        final Spinner unitSpinner = dialog.findViewById(R.id.unitSpinner);
        Button modifyButton = dialog.findViewById(R.id.addButton);

        nameEditText.setText(grocery.getName());
        quantityEditText.setText(String.valueOf(grocery.getQuantity()));

        String[] units = new String[]{"Select Unit", "kg", "g", "lbs", "oz", "ml", "l", "cup"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, units);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerAdapter);

        int spinnerPosition = spinnerAdapter.getPosition(grocery.getUnit());
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

                    grocery.setName(name);
                    grocery.setQuantity(quantity);
                    grocery.setUnit(unit);

                    databaseReference.child(grocery.getId()).setValue(grocery);

                    Toast.makeText(requireContext(), "Grocery modified!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showAddGroceryDialog() {
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String quantityStr = quantityEditText.getText().toString().trim();
                String unit = unitSpinner.getSelectedItem().toString().trim();


                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unit.equalsIgnoreCase("Select Unit")) {
                    double quantity = Double.parseDouble(quantityStr);

                    String groceryId = databaseReference.push().getKey();
                    Grocery grocery = new Grocery(groceryId, name, quantity, unit,false);

                    if (groceryId != null) {
                        databaseReference.child(groceryId).setValue(grocery);
                        Toast.makeText(requireContext(), "Grocery added!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }


    private void showAddCheckDialog(final Grocery grocery,final Boolean isChecked) {



        if (isChecked) {
            // The checkbox is checked, perform the desired action
            String name = grocery.getName();
            Double quantity = grocery.getQuantity();
            String unit = grocery.getUnit();
            grocery.setCheck(true);
            databaseReference.child(grocery.getId()).setValue(grocery);

            String ingredientId = databaseReferenceInv.push().getKey();
            Ingredient ingredient = new Ingredient(ingredientId, name, quantity, unit);

            if (ingredientId != null) {
                databaseReferenceInv.child(ingredientId).setValue(ingredient);
                Toast.makeText(requireContext(), "Ingredient added!", Toast.LENGTH_SHORT).show();

            }

        }



    }

    private void updateGrocery() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing ingredients
                adapter.clearGrocery();
                // Add the ingredients from the database
                adapter.setGrocery(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors

            }
        });
    }
}

