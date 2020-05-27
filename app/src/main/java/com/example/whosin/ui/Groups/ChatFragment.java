package com.example.whosin.ui.Groups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.FirebaseActions;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.MessageChat;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment  {

    private EditText editText;
    private Group group;
    private User user;
    //private ChatViewModel chatViewModel;
    FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;


    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        user = CurrentUser.getInstance();
        group = getArguments().getParcelable("group");
       // chatViewModel.init(this,group);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = root.findViewById(R.id.messages_view);
        recyclerView.setHasFixedSize(true);
        editText = root.findViewById(R.id.editTextMessageText);
        ImageView send = root.findViewById(R.id.send_message_button);

        loadMessages();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().equals("")) {
                    try {
                        FirebaseActions.sendMessage(group,editText.getText().toString());
                        Snackbar.make(root,"Message Sent",Snackbar.LENGTH_SHORT).show();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        editText.setText("");
                    } catch (Exception e){
                        Snackbar.make(root,"Error",Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

        });
//        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        return root;
    }


    private void loadMessages() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Groups").child(group.getId())
                .child("Chat")
                .child("Messages").limitToLast(500);

        Log.d("On Bind ++++++++++", "onBindViewHolder: ");
        FirebaseRecyclerOptions<MessageChat> options =
                new FirebaseRecyclerOptions.Builder<MessageChat>().setQuery(query,MessageChat.class).build();

        adapter = new FirebaseRecyclerAdapter<MessageChat, RecyclerView.ViewHolder>(options) {
            private int TYPE_OWN = 1;
            private int TYPE_OTHER = 2;

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == TYPE_OWN){
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_massage_sender,parent,false);
                    return new ChatOwnViewHolder(view);
                }else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_receiver,parent,false);
                    return new ChatOtherViewHolder(view);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull final MessageChat model) {
                Log.d("On Bind ++++++++++", "onBindViewHolder: " + model.toString());
                if (getItemViewType(position) == TYPE_OWN) {
                    ((ChatOwnViewHolder)holder).textViewContext.setText(model.getContext());
                    ((ChatOwnViewHolder)holder).textViewHour.setText(model.getHour());

                }else {
                    Glide.with(getActivity()).load(model.getImageUrl()).into(((ChatOtherViewHolder)holder).circleImageView);
                    ((ChatOtherViewHolder)holder).textViewName.setText(model.getSender());
                    ((ChatOtherViewHolder)holder).textViewContext.setText(model.getContext());
                    ((ChatOtherViewHolder)holder).textViewHour.setText(model.getHour());
                }
            }

            @Override
            public int getItemCount() {
                return getSnapshots().size();
            }

            @Override
            public int getItemViewType(int position) {
                MessageChat messageChat = this.getSnapshots().get(position);
                if (messageChat.getSenderID().equals(user.getEmail())) {
                    return TYPE_OWN;
                } else {
                    return TYPE_OTHER;
                }
            }
        };

        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        //
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
class ChatOtherViewHolder extends RecyclerView.ViewHolder {
    TextView textViewContext , textViewHour , textViewName;
    CircleImageView circleImageView;

    public ChatOtherViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName  = itemView.findViewById(R.id.textViewMessageContextName);
        textViewContext = itemView.findViewById(R.id.textViewContext);
        textViewHour = itemView.findViewById(R.id.textViewHour);
        circleImageView = itemView.findViewById(R.id.circleImageViewProfileSender);
    }
}

class ChatOwnViewHolder extends RecyclerView.ViewHolder {
    TextView textViewContext , textViewHour;

    public ChatOwnViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewContext = itemView.findViewById(R.id.textViewContext2);
        textViewHour = itemView.findViewById(R.id.textViewHour2);
    }
}
