package com.example.techtalk.requests;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techtalk.Common.Constants;
import com.example.techtalk.Common.NodeNames;
import com.example.techtalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Node;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<RequestModel> requestModelList;
    private DatabaseReference databaseReferenceFriendRequests,databaseReferenceChats;
    private FirebaseUser currentUser;

    public RequestAdapter(Context context, List<RequestModel> requestModelList) {
        this.context = context;
        this.requestModelList = requestModelList;
    }

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.friend_request_layout,parent,false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestAdapter.RequestViewHolder holder, int position) {
        final RequestModel requestModel=requestModelList.get(position);
        holder.tvFullName.setText(requestModel.getUserName());
        StorageReference fileRef= FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER+"/"+requestModel.getPhotoName());
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri).placeholder(R.drawable.avatar).into(holder.ivProfile);
            }
        });

        databaseReferenceFriendRequests= FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUESTS);
        databaseReferenceChats=FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        holder.btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.pbDecision.setVisibility(View.VISIBLE);
                holder.btnDeleteRequest.setVisibility(View.GONE);
                holder.btnAcceptRequest.setVisibility(View.GONE);

                final String userId=requestModel.getUserId();
                databaseReferenceChats.child(currentUser.getUid()).child(userId)
                        .child(NodeNames.TIME_STAMP).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            databaseReferenceChats.child(userId).child(currentUser.getUid())
                                    .child(NodeNames.TIME_STAMP).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        databaseReferenceFriendRequests.child(currentUser.getUid()).child(userId)
                                                .child(NodeNames.REQUEST_TYPE).setValue(Constants.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    databaseReferenceFriendRequests.child(userId).child(currentUser.getUid())
                                                            .child(NodeNames.REQUEST_TYPE).setValue(Constants.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){

                                                                holder.pbDecision.setVisibility(View.GONE);
                                                                holder.btnDeleteRequest.setVisibility(View.VISIBLE);
                                                                holder.btnAcceptRequest.setVisibility(View.VISIBLE);
                                                            }
                                                            else{
                                                                handleException(holder,task.getException());
                                                            }
                                                        }
                                                    });

                                                }
                                                else{
                                                    handleException(holder,task.getException());
                                                }
                                            }
                                        });

                                    }
                                    else{
                                        handleException(holder,task.getException());
                                    }
                                }
                            });



                        }
                        else{
                            handleException(holder,task.getException());

                        }
                    }
                });


            }
        });

        holder.btnDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.pbDecision.setVisibility(View.VISIBLE);
                holder.btnDeleteRequest.setVisibility(View.GONE);
                holder.btnAcceptRequest.setVisibility(View.GONE);

                final String userId=requestModel.getUserId();
                databaseReferenceFriendRequests.child(currentUser.getUid()).child(userId)
                        .child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            databaseReferenceFriendRequests.child(userId).child(currentUser.getUid())
                                    .child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        holder.pbDecision.setVisibility(View.GONE);
                                        holder.btnDeleteRequest.setVisibility(View.VISIBLE);
                                        holder.btnAcceptRequest.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Toast.makeText(context,context.getString(R.string.failed_to_delete_request,task.getException()),
                                                Toast.LENGTH_SHORT).show();

                                        holder.pbDecision.setVisibility(View.GONE);
                                        holder.btnDeleteRequest.setVisibility(View.VISIBLE);
                                        holder.btnAcceptRequest.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(context,context.getString(R.string.failed_to_delete_request,task.getException()),
                                    Toast.LENGTH_SHORT).show();
                            holder.pbDecision.setVisibility(View.GONE);
                            holder.btnDeleteRequest.setVisibility(View.VISIBLE);
                            holder.btnAcceptRequest.setVisibility(View.VISIBLE);

                        }
                    }
                });



            }
        });
    }

    private void handleException(RequestViewHolder holder,Exception exception) {
        Toast.makeText(context,context.getString(R.string.failed_to_accept_request,exception),Toast.LENGTH_SHORT).show();
        holder.pbDecision.setVisibility(View.GONE);
        holder.btnDeleteRequest.setVisibility(View.VISIBLE);
        holder.btnAcceptRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return requestModelList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFullName;
        private ImageView ivProfile;
        private Button btnAcceptRequest,btnDeleteRequest;
        private ProgressBar pbDecision;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName=itemView.findViewById(R.id.tvFullName);
            ivProfile=itemView.findViewById(R.id.ivProfile);
            btnAcceptRequest=itemView.findViewById(R.id.btnAcceptRequest);
            btnDeleteRequest=itemView.findViewById(R.id.btnDeleteRequest);
            pbDecision=itemView.findViewById(R.id.pbDecision);
        }
    }
}
