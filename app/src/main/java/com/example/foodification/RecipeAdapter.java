package com.example.foodification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    // Constructor
    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }
    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    // ViewHolder class
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeTitle, prepTime, totalTime;
        public Button openButton;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            prepTime = itemView.findViewById(R.id.prepTime);
            totalTime = itemView.findViewById(R.id.totalTime);
            openButton = itemView.findViewById(R.id.button1);
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.recipeTitle.setText(recipe.getName());
        holder.prepTime.setText(String.format("Prep time: %s", recipe.getPrepTime()));
        holder.totalTime.setText(String.format("Total time: %s", recipe.getTotalTime()));

        // Here we use the 'position' parameter to get the current item's position
        holder.openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(context, RecipeDetailActivity.class);

                // Make sure Recipe class implements Serializable or Parcelable
                detailIntent.putExtra("RecipeData", recipe);

                // Flag is needed if you're starting an activity from outside of an activity context
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(detailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }
}

