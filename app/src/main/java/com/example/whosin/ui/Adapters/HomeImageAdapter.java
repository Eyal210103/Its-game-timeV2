package com.example.whosin.ui.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosin.R;
import com.example.whosin.model.ImageResources;

import java.util.ArrayList;

public class HomeImageAdapter  extends RecyclerView.Adapter<HomeImageAdapter.ViewHolder> {

    private ArrayList<ImageResources> imageResource;
    private Context mContext;
    private FragmentManager fragmentManager;

    public HomeImageAdapter(ArrayList<ImageResources> imageResources, Context context, FragmentManager fragmentManager) {
        this.imageResource = imageResources;
        this.mContext = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_buttons,parent,false);
        return new HomeImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int r = imageResource.get(position).getImageR();
        holder.imageView.setImageResource(r);
        holder.imageView.setBackgroundResource(imageResource.get(position).getBackR());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToFragment(imageResource.get(position).getFragmentToGo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageResource.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.home_image_button);
        }
    }
    private void swapToFragment(Fragment nextFrag) {
        Bundle bundle = new Bundle();
        nextFrag.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace((R.id.container_fragments), nextFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
