package com.example.foodification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    private List<Grocery> grocerys;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public GroceryAdapter(Context context, List<Grocery> grocery, OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.grocerys = grocery;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_grocery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Grocery grocery = grocerys.get(position);
        holder.bind(grocery, listener);
    }

    @Override
    public int getItemCount() {
        return grocerys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView quantityTextView;
        private TextView unitTextView;
        private Button deleteButton;
        private Button modifyButton;
        private CheckBox groceryCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            unitTextView = itemView.findViewById(R.id.unitTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            modifyButton = itemView.findViewById(R.id.modifyButton);
            groceryCheck = itemView.findViewById(R.id.groceryCheck);
        }

        public void bind(final Grocery grocery, final OnItemClickListener listener) {
            nameTextView.setText(grocery.getName());
            quantityTextView.setText(String.valueOf(grocery.getQuantity()));
            unitTextView.setText(grocery.getUnit());
            groceryCheck.setChecked(grocery.getCheck());

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
            groceryCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        // The checkbox is checked, call the private method
                        listener.showOnCheck(getAdapterPosition(),isChecked);

                }
            });
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onModifyClick(int position);
        void showOnCheck(int position,Boolean isChecked);
    }

    // Setter method to update the ingredients list
    public void setGrocery(Iterable<DataSnapshot> grocerySnapshots) {
        grocerys.clear();
        for (DataSnapshot snapshot : grocerySnapshots) {
            Grocery grocery = snapshot.getValue(Grocery.class);
            grocerys.add(grocery);
        }
        notifyDataSetChanged();
    }
    public void clearGrocery() {
        grocerys.clear();
        notifyDataSetChanged();
    }
    public Grocery getGrocery(int position) {
        return grocerys.get(position);
    }
}
