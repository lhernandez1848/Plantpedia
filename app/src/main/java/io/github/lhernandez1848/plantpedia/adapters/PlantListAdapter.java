package io.github.lhernandez1848.plantpedia.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import io.github.lhernandez1848.plantpedia.R;
import io.github.lhernandez1848.plantpedia.SelectedPlantActivity;
import io.github.lhernandez1848.plantpedia.models.Plant;

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.PlantHolder>{

    private Context context;
    private ArrayList<Plant> plants;

    public PlantListAdapter(Context context, ArrayList<Plant> plants) {
        this.context = context;
        this.plants = plants;
    }

    public class PlantHolder extends RecyclerView.ViewHolder {

        private TextView tvPlantName, tvComment;
        private ImageView allImageView;

        public PlantHolder(View itemView) {
            super(itemView);

            tvPlantName = (TextView) itemView.findViewById(R.id.nameTextView);
            tvComment = (TextView) itemView.findViewById(R.id.commentsTextView);
            allImageView = (ImageView) itemView.findViewById(R.id.allImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // save plant details to intent
                    Intent intent = new Intent(context, SelectedPlantActivity.class);
                    intent.putExtra("plantName", tvPlantName.getText().toString());
                    intent.putExtra("activity", context.getClass().getSimpleName());

                    // call order details activity
                    context.startActivity(intent);
                }
            });
        }

        public void setDetails(Plant plant) {

            String name = plant.getName();
            String comment = plant.getComment();
            String image = plant.getImage();
            Uri imageUri = Uri.parse(image);

            tvPlantName.setText(name);
            tvComment.setText(comment);

            try {
                ContentResolver contentResolver = context.getContentResolver();
                ImageDecoder.Source imageSrc = ImageDecoder.createSource(contentResolver, imageUri);
                Drawable drawable = ImageDecoder.decodeDrawable(imageSrc);
                allImageView.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public PlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.plant_row, parent, false);
        return new PlantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.setDetails(plant);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }
}
