package com.example.dragtest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.viewHolder> {

    Context context;
    ArrayList<item> arrayList;

    public CustomAdapter(Context context, ArrayList<item> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.viewHolder viewHolder, int position) {

        viewHolder.levelName.setText(arrayList.get(position).getName());
        viewHolder.levelImage.setImageResource(arrayList.get(position).getImage());
        final String levelName = arrayList.get(position).getName();
        viewHolder.levelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("LevelName", levelName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView levelImage;
        TextView levelName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            levelImage = itemView.findViewById(R.id.levelImage);
            levelName = itemView.findViewById(R.id.levelName);
        }
    }
}
