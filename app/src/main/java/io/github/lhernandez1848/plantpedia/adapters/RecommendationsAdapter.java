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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import io.github.lhernandez1848.plantpedia.GlobalMethods;
import io.github.lhernandez1848.plantpedia.R;
import io.github.lhernandez1848.plantpedia.SelectedPlantActivity;
import io.github.lhernandez1848.plantpedia.models.Plant;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationsHolder>{

    private Context context;
    private ArrayList<Plant> plants;
    private RecAdapterListener recAdapterListener;

    public RecommendationsAdapter(Context context, ArrayList<Plant> plants) {
        this.context = context;
        this.plants = plants;
    }

    public class RecommendationsHolder extends RecyclerView.ViewHolder {

        private TextView tvPlantName;
        private ImageView allImageView;
        private ImageButton btnRecWatered;
        private String plantName;
        private GlobalMethods globalMethods;

        public RecommendationsHolder(final View itemView) {
            super(itemView);

            tvPlantName = (TextView) itemView.findViewById(R.id.nameRecTextView);
            allImageView = (ImageView) itemView.findViewById(R.id.allRecImageView);
            btnRecWatered = (ImageButton) itemView.findViewById(R.id.btnRecWatered);

            globalMethods = new GlobalMethods(context);

            plantName = "";

            btnRecWatered.setOnClickListener(v -> {
                System.out.println(v);
                recAdapterListener.onWatered(v, plantName);
                System.out.println(plantName);
            });

            itemView.setOnClickListener(v -> {

                // save plant details to intent
                Intent intent = new Intent(context, SelectedPlantActivity.class);
                intent.putExtra("plantName", tvPlantName.getText().toString());
                intent.putExtra("activity", context.getClass().getSimpleName());

                // call activity
                context.startActivity(intent);
            });
        }

        public void setDetails(Plant plant) {

            String name = plant.getName();
            String image = plant.getImage();
            Uri imageUri = Uri.parse(image);

            tvPlantName.setText(name);
            plantName = name;

            if (globalMethods.checkStoragePermission()){
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

    }

    @NonNull
    @Override
    public RecommendationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.listview_item, parent, false);
        return new RecommendationsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationsHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.setDetails(plant);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public void SetOnRecAdapterListener(final RecAdapterListener recAdapterListener){
        this.recAdapterListener = recAdapterListener;
    }

    public interface RecAdapterListener {
        void onWatered(View view, String name);
    }
}
