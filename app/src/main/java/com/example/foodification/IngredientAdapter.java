package com.example.foodification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> ingredients;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public IngredientAdapter(Context context, List<Ingredient> ingredients, OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient, listener);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView quantityTextView;
        private TextView unitTextView;
        private Button deleteButton;
        private Button modifyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            unitTextView = itemView.findViewById(R.id.unitTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            modifyButton = itemView.findViewById(R.id.modifyButton);
        }

        public void bind(final Ingredient ingredient, final OnItemClickListener listener) {
            nameTextView.setText(ingredient.getName());
            quantityTextView.setText(String.valueOf(ingredient.getQuantity()));
            unitTextView.setText(ingredient.getUnit());

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });

            modifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onModifyClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onModifyClick(int position);
    }

    // Setter method to update the ingredients list
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
    public Ingredient getIngredient(int position) {
        return ingredients.get(position);
    }
}
