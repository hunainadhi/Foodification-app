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
    private Context context;

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

        if (recipe.getPrepTime() != null && !recipe.getPrepTime().isEmpty()) {
            holder.prepTime.setText(String.format("Prep time: %s", recipe.getPrepTime()));
        } else {
            holder.prepTime.setText("Prep time: N/A");
        }

        if (recipe.getTotalTime() != null && !recipe.getTotalTime().isEmpty()) {
            holder.totalTime.setText(String.format("Total time: %s", recipe.getTotalTime()));
        } else {
            holder.totalTime.setText("Total time: N/A");
        }

        String imageUrl = recipe.getImage();
        Picasso.get().load(imageUrl).into(holder.recipeImage);

        holder.openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(context, RecipeDetailActivity.class);
                detailIntent.putExtra("RecipeData", recipe);
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(detailIntent);
            }
        });
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
