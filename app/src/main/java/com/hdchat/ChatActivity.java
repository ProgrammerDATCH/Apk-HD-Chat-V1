package com.hdchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    String givenNames,givenCode;
    TextInputEditText typedMsg;
    MaterialButton sendBtn;
    private DatabaseReference root;
    String temp_Key;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        typedMsg = findViewById(R.id.typedMsg);
        sendBtn = findViewById(R.id.sendMsgBtn);
        recyclerView = findViewById(R.id.chatRecyclerView);

        givenNames = getIntent().getStringExtra("givenNames");
        givenCode = getIntent().getStringExtra("givenCode");

        root = FirebaseDatabase.getInstance().getReference().child(givenCode);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendBtn.setOnClickListener((v) -> {
            String givenMsg = typedMsg.getText().toString();
            if(givenMsg.isEmpty())
            {
                typedMsg.setError("Type Message Please!");
                typedMsg.requestFocus();
                return;
            }

            Map<String,Object> map = new HashMap<String,Object>();
            temp_Key=root.push().getKey();
            root.updateChildren(map);

            DatabaseReference message_root = root.child(temp_Key);
            Map<String,Object> map2 = new HashMap<String,Object>();
            map2.put("user", givenNames);
            map2.put("msg",givenMsg);
            message_root.updateChildren(map2);
            typedMsg.setText("");
        });

        //show to andoid
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                appendToChat(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                appendToChat(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private String chatMsg, chatUser;
    private void appendToChat(DataSnapshot snapshot)
    {
        Iterator i = snapshot.getChildren().iterator();
        while (i.hasNext())
        {
            chatMsg = (String) ((DataSnapshot)i.next()).getValue();
            chatUser = (String) ((DataSnapshot)i.next()).getValue();
            if(givenNames.equals(chatUser))
            {
                addToChat(chatMsg,Message.SENT_BY_ME);
            }
            else
            {
                addToChat(chatUser + ": " + chatMsg,Message.SENT_BY_BOT);
            }

        }

    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
}