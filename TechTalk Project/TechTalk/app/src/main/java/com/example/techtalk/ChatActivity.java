package com.example.techtalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.techtalk.Common.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivSend;
    private EditText etMessage;
    private DatabaseReference mRootRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserId, chatUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ivSend=findViewById(R.id.ivSend);
        etMessage=findViewById(R.id.etMessage);

        ivSend.setOnClickListener(this);

        firebaseAuth=FirebaseAuth.getInstance();
        mRootRef= FirebaseDatabase.getInstance().getReference();
        currentUserId=firebaseAuth.getCurrentUser().getUid();

    }

    private void sendMessage(String msg,String msgType,String pushId){
        try{
            if(!msg.equals("")){

                HashMap messageMap=new HashMap();
                messageMap.put(NodeNames.MESSAGE_ID,pushId);
                messageMap.put(NodeNames.MESSAGE,msg);
                messageMap.put(NodeNames.MESSAGE_TYPE,msgType);
                messageMap.put(NodeNames.MESSAGE_FROM,currentUserId);
                messageMap.put(NodeNames.MESSAGE_TIME, ServerValue.TIMESTAMP);

                String currentUserRef=NodeNames.MESSAGES+"/"+currentUserId+"/"+chatUserId;
                String chatUserRef=NodeNames.MESSAGES+"/"+chatUserId+"/"+chatUserId;

                HashMap messageUserMap=new HashMap();
                messageUserMap.put(currentUserRef+"/"+pushId,messageMap);
                messageUserMap.put(chatUserRef+"/"+pushId,messageMap);

                etMessage.setText("");

                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error!=null){
                            Toast.makeText(ChatActivity.this,"",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
        catch(Exception ex){
    }

    @Override
    public void onClick(View view) {


    }
}