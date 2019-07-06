package com.example.hyl.person;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



public class MakeNotes extends AppCompatActivity {
    private SQLiteDatabase db;
    private ImageButton add_save,add_cancle;
    private EditText add_money,add_notes;
    private Spinner spin;
    private String userID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        setContentView(R.layout.add_note_layout);
        add_save = (ImageButton)findViewById(R.id.add_save);
        add_cancle = (ImageButton)findViewById(R.id.add_cancle);
        add_money = (EditText)findViewById(R.id.add_money);
        add_notes = (EditText)findViewById(R.id.add_notes);
        spin = (Spinner)findViewById(R.id.spin_income);
        db = openOrCreateDatabase("money.db", MODE_PRIVATE, null);
        db.execSQL("create table if not exists moneytb (_id int primary key , money double , time time not null ,userID text not null, state text ,isDelete text)");


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        add_money.setTextColor(Color.RED);
                        TextView tv = (TextView)view;
                        tv.setTextColor(Color.GRAY);
                    }break;
                    default:{
                        add_money.setTextColor(Color.GREEN);
                        TextView tv = (TextView)view;
                        tv.setTextColor(Color.GRAY);
                    }break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_money.selectAll();
            }
        });
        add_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql = "select * from moneytb";
                Cursor c = db.rawQuery(sql,null);
                int  num = c.getCount()+1;
                double mMoney = Double.valueOf(add_money.getText().toString());
                if(spin.getSelectedItem().equals("支出"))
                    mMoney *=-1;
                String values = "values( '" + num + "','" +mMoney+ "','" + getTime() + "','" + userID+ "','" +add_notes.getText().toString()+ "','" + "false"+ "')";
                db.execSQL("insert into moneytb(_id,money,time,userID,state,isDelete) " + values);
                Dialog progressDialog= new Dialog(MakeNotes.this,R.style.progress_dialog);
                progressDialog.setContentView(R.layout.load_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
                msg.setText("正在保存数据...");
                progressDialog.show();
                TimerTask finishTask = new TimerTask(){
                    public void run(){
                        //execute the task
                        finish();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(finishTask , 1000);
            }



        });
        add_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

}

