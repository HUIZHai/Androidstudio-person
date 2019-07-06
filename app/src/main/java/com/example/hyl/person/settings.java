package com.example.hyl.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class settings extends AppCompatActivity {
    TextView set_exit_back;
    TextView set_ID;
    TextView changePwd;
    TextView set_exit;
    TextView weather;
    TextView music;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        set_ID = (TextView)findViewById(R.id.set_ID);
        set_ID.setText(userID);
        changePwd = (TextView)findViewById(R.id.changePwd);
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("userID", userID);
                intent.setClass(settings.this,change_pwd.class);
                startActivity(intent);

            }
        });
        weather = (TextView) findViewById(R.id.hz_weather);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(settings.this,weatherActivity.class);
                startActivity(intent);
            }
        });
        music = (TextView) findViewById(R.id.mymusic);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(settings.this,musicActivity.class);
                startActivity(intent);
            }
        });

        set_exit = (TextView)findViewById(R.id.set_exit);
        set_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(settings.this,LoginActivity.class);
                startActivity(intent2);

            }
        });

        set_exit_back = (TextView)findViewById(R.id.set_text_back);
        set_exit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
