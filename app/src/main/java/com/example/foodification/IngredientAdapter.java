package com.example.foodification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private Context context;
    private List<Ingredient> ingredients;
    private OnItemClickListener itemClickListener;
    private int selectedItemPosition = -1;

    public IngredientAdapter(Context context) {
        this.context = context;
        this.ingredients = new ArrayList<>();
    }

    public void setIngredients(Iterable<DataSnapshot> ingredientSnapshots) {
        ingredients.clear();
        for (DataSnapshot snapshot : ingredientSnapshots) {
            Ingredient ingredient = snapshot.getValue(Ingredient.class);
            ingredients.add(ingredient);
        }
        notifyDataSetChanged();
    }

    public void clearIngredients() {
        ingredients.clear();
        notifyDataSetChanged();
    }
    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public Ingredient getIngredient(int position) {
        return ingredients.get(position);
    }

    public void clearSelectedItem() {
        selectedItemPosition = -1;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.nameTextView.setText(ingredient.getName());
        holder.quantityTextView.setText(String.valueOf(ingredient.getQuantity()));
        holder.unitTextView.setText(ingredient.getUnit());

        // Highlight the selected item
        if (selectedItemPosition == position) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.selected_item_color));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        // Set a click listener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                setSelectedItemPosition(clickedPosition);

                if (itemClickListener != null) {
                    itemClickListener.onItemClick(clickedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView quantityTextView;
        TextView unitTextView;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            unitTextView = itemView.findViewById(R.id.unitTextView);
        }
    }

    // Method to set the selected item position
    public void setSelectedItemPosition(int position) {
        selectedItemPosition = position;
        notifyDataSetChanged();
    }

    // Interface for item click callbacks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
