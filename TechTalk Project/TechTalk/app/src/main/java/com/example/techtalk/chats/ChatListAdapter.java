package com.example.techtalk.chats;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techtalk.Common.Constants;
import com.example.techtalk.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private Context context;
    private List<ChatListModel> chatListModelList;

    public ChatListAdapter(Context context, List<ChatListModel> chatListModelList) {
        this.context = context;
        this.chatListModelList = chatListModelList;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chat_list_layout,parent,false);

        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, int position) {

        ChatListModel chatListModel=chatListModelList.get(position);
        holder.tvFullName.setText(chatListModel.getUserName());

        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER + "/" + chatListModel.getPhotoName());
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.avatar).error(R.drawable.avatar).into(holder.ivProfile);
            }
        });



    }

    @Override
    public int getItemCount() {
        return chatListModelList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llChatList;
        private TextView tvFullName,tvLastMessage,tvLastMessageTime,tvUnreadCount;
        private ImageView ivProfile;


        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            llChatList=itemView.findViewById(R.id.llChatList);
            tvFullName=itemView.findViewById(R.id.tvFullName);
            tvLastMessage=itemView.findViewById(R.id.tvLastMessage);
            tvLastMessageTime=itemView.findViewById(R.id.tvLastMessageTime);
            tvUnreadCount=itemView.findViewById(R.id.tvUnreadCount);
            ivProfile=itemView.findViewById(R.id.ivProfile);


        }
    }
}
