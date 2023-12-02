package com.example.foodification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;    private RecipePageFragment fragment;

    public RecipeAdapter(RecipePageFragment fragment, List<Recipe> recipeList) {
        this.fragment = fragment;
        this.recipeList = recipeList;
    }

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeTitle, prepTime, totalTime;
        public Button openButton;
        public ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            prepTime = itemView.findViewById(R.id.prepTime);
            totalTime = itemView.findViewById(R.id.totalTime);
            openButton = itemView.findViewById(R.id.button1);
            recipeImage = itemView.findViewById(R.id.recipeImage);
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

        if (recipe.getmissPer() != null && !recipe.getmissPer().isEmpty()) {
            holder.prepTime.setText(String.format("Percentage of missed Ingredients: %s", recipe.getmissPer()));
        } else {
            holder.prepTime.setText("Percentage of missed Ingredients: N/A");
        }

        if (recipe.getmissCount() != null && !recipe.getmissCount().isEmpty()) {
            holder.totalTime.setText(String.format("Count of Missed Ingredients: %s", recipe.getmissCount()));
        } else {
            holder.totalTime.setText("Count of Missed Ingredients: N/A");
        }

        String imageUrl = recipe.getImage();
        Picasso.get().load(imageUrl).into(holder.recipeImage);

        holder.openButton.setOnClickListener(v -> fragment.onRecipeClicked(recipe));

    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }
}
