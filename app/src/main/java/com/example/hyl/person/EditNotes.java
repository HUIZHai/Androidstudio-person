package com.example.hyl.person;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

/**
 * Created by Roelin on 2017/7/2.
 */

public class EditNotes extends AppCompatActivity {
    private SQLiteDatabase db;
    private ImageButton edit_save,edit_back,edit_delete;
    private EditText edit_money,edit_notes;
    private Spinner spin;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_layout);
        Intent intent = getIntent();
        final int id = Integer.valueOf(intent.getStringExtra("ID"));
        edit_save = (ImageButton)findViewById(R.id.edit_save);
        edit_back = (ImageButton)findViewById(R.id.edit_cancle);
        edit_delete = (ImageButton)findViewById(R.id.edit_delete);
        edit_money = (EditText)findViewById(R.id.edit_money);
        edit_notes = (EditText)findViewById(R.id.edit_notes);
        spin = (Spinner)findViewById(R.id.spin_income);
        db = openOrCreateDatabase("money.db", MODE_PRIVATE, null);
        db.execSQL("create table if not exists moneytb (_id int primary key , money double , time time not null ,userID text not null, state text ,isDelete text)");

        String sql = "select * from moneytb where _id = " + id;
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToNext()) {
            String notes = c.getString(c.getColumnIndex("state"));
            Double money = c.getDouble(c.getColumnIndex("money"));
            edit_notes.setText(notes);
            if(money>0){
                edit_money.setText(money.toString());
                edit_money.setTextColor(Color.RED);
                spin.setSelection(0);
            }else{
                money *= -1;
                edit_money.setText(money.toString());
                edit_money.setTextColor(Color.GREEN);
                spin.setSelection(1);
            }

        }

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        edit_money.setTextColor(Color.RED);
                        TextView tv = (TextView)view;
                        tv.setTextColor(Color.GRAY);
                    }break;
                    default:{
                        edit_money.setTextColor(Color.GREEN);
                        TextView tv = (TextView)view;
                        tv.setTextColor(Color.GRAY);
                    }break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edit_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_money.selectAll();
            }
        });

        edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double money = Double.valueOf(edit_money.getText().toString());
                if(spin.getSelectedItem().equals("支出"))
                    money *=-1;
                db.execSQL("update moneytb set state = '" + edit_notes.getText() + "' , money = " + money +" where _id = " + id );
                Dialog progressDialog= new Dialog(EditNotes.this,R.style.progress_dialog);
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

        edit_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDelete(id);
                //db.execSQL("update moneytb set isdelete = 'true' where _id = " + id );
                //      finish();
            }
        });

        edit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void isDelete(int id){
        final int mID = id;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除")
                .setMessage("确认删除?")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("update moneytb set isdelete = 'true' where _id = " + mID );
                                Dialog progressDialog= new Dialog(EditNotes.this,R.style.progress_dialog);
                                progressDialog.setContentView(R.layout.load_dialog);
                                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
                                msg.setText("删除中，请等待...");
                                progressDialog.show();
                                TimerTask finishTask = new TimerTask(){
                                    public void run(){
                                        //execute the task
                                        finish();
                                    }
                                };
                                Timer timer = new Timer();
                                timer.schedule(finishTask , 3000);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        AlertDialog ad = builder.create();
        ad.show();
    }

}
