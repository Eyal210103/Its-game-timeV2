package com.example.whosin.ui.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.User;
import com.example.whosin.ui.Groups.GroupInfoFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private ArrayList<Group> mGroups;
    private User user;
    private Context mContext;
    private FragmentManager fragmentManager;
    private String isThere;



    public GroupsAdapter(ArrayList<Group> mGroups, User user, Context mContext, FragmentManager fragmentManager, String isThere) {
        this.mGroups = mGroups;
        this.user = user;
        this.fragmentManager = fragmentManager;
        this.mContext = mContext;
        this.isThere = isThere;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_groups,parent,false);
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Group group = mGroups.get(position);
        Glide.with(mContext).load(group.getImage()).into(holder.circleImageView);
        holder.textViewName.setText(group.getGroupName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupInfoFragment nextFrag = new GroupInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("group" , mGroups.get(position));
                bundle.putString("view" , isThere);
                nextFrag.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace((R.id.container_fragments), nextFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
    private void removeItem(int position) {
        mGroups.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mGroups.size());
    }

    @Override
    public int getItemCount() {
        try {
            return mGroups.size();
        }catch (Exception ignored){
            return  0;
        }
    }

    public void setMGroups(ArrayList<Group> mGroups) {
        this.mGroups = mGroups;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView textViewName , textViewSports;
        CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName  = itemView.findViewById(R.id.list_groupName);
            circleImageView  = itemView.findViewById(R.id.groupLogo);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            final int index = this.getAdapterPosition();
            menu.add(index,0,0,"Leave Group").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Group group = mGroups.get(index);
                    removeItem(index);
                    return FirebaseActions.leaveGroup(group);
                }
            });
        }
    }
}

