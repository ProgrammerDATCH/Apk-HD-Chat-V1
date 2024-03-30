package com.hdchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextInputEditText fullName, secretCode;
    MaterialButton startChat;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("name_pref", MODE_PRIVATE);

        fullName = findViewById(R.id.name);
        secretCode = findViewById(R.id.code);
        startChat = findViewById(R.id.start_chat);

        fullName.setText(sharedPreferences.getString("name", ""));

        startChat.setOnClickListener((v)->
        {
            String givenNames = fullName.getText().toString();
            if(givenNames.isEmpty())
            {
                fullName.setError("Name is required to start a Chat");
                fullName.requestFocus();
                return;
            }
            String givenCode = secretCode.getText().toString();
            if(givenCode.isEmpty())
            {
                secretCode.setError("Login Secret Code is required please!");
                secretCode.requestFocus();
                return;
            }
            if(!loginSuccess(givenCode))
            {
                //login failed
                secretCode.setError("Wrong Secret code, Try again!");
                secretCode.requestFocus();
              return;
            }

            //start Chatting
            startChatting(givenNames, givenCode);
        });

    }

    public void startChatting(String givenNames, String givenCode)
    {
        sharedPreferences.edit().putString("name", givenNames).apply();
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("givenNames", givenNames);
        intent.putExtra("givenCode", givenCode);
        startActivity(intent);
    }
    public boolean loginSuccess(String givenCode)
    {
        if(givenCode.equals("hd"))
        {
            return true;
        }
        else if(givenCode.equals("mut"))
        {
            return true;
        }
        else if(givenCode.equals("1502"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}