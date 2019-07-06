package com.example.hyl.person;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by macbook air on 2017/7/3.
 */

public class InfomationActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Spinner spin_sex;
    private String ID;
    TextView info_back;
    TextView info_kefu;
    TextView info_exit_ID;
    EditText info_exit_Age;
    EditText info_exit_Telephone;
    EditText info_exit_Address;
    EditText info_exit_Email;
    //    EditText info_exit_Sex;
    EditText info_exit_Name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        ID = userID;
        info_kefu = (TextView) findViewById(R.id.kefu);
        info_kefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(InfomationActivity.this, kefu.class);
                startActivity(intent);

            }
        });
        onResume();
        info_exit_ID = (TextView) findViewById(R.id.exit_info_ID);
        info_exit_ID.setText(userID);

        spin_sex = (Spinner)findViewById(R.id.spin_sex);

        db = openOrCreateDatabase("user_data", MODE_PRIVATE, null);
        String sql = "select * from users_new where _id = '" + userID + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            String username = cursor.getString(cursor.getColumnIndex("user_name"));
            String usertelephone = cursor.getString(cursor.getColumnIndex("user_telephone"));
            String userage = cursor.getString(cursor.getColumnIndex("user_age"));
            String useraddress = cursor.getString(cursor.getColumnIndex("user_address"));
            String useremail = cursor.getString(cursor.getColumnIndex("user_email"));
            String usersex = cursor.getString(cursor.getColumnIndex("user_sex"));

            info_exit_Name = (EditText) findViewById(R.id.exit_info_name);
            info_exit_Name.setText(username);

            info_exit_Telephone = (EditText) findViewById(R.id.exit_info_telephone);
            info_exit_Telephone.setText(usertelephone);

            info_exit_Age = (EditText) findViewById(R.id.exit_info_age);
            info_exit_Age.setText(userage);

            info_exit_Email = (EditText) findViewById(R.id.exit_info_email);
            info_exit_Email.setText(useremail);

            info_exit_Address = (EditText) findViewById(R.id.exit_info_address);
            info_exit_Address.setText(useraddress);

            spin_sex = (Spinner)findViewById(R.id.spin_sex);
            if(usersex.equals("男")){
                spin_sex.setSelection(0);
            }else{
                spin_sex.setSelection(1);
            }
        }

        info_back = (TextView) findViewById(R.id.info_text_back);
        info_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
                onResume();
                finish();
            }
        });
    }

    public void  updateUserData() {
        String username = info_exit_Name.getText().toString().trim();
        String usertelephone = info_exit_Telephone.getText().toString().trim();
        String userage = info_exit_Age.getText().toString().trim();
        String useraddress = info_exit_Address.getText().toString().trim();
        String useremail = info_exit_Email.getText().toString().trim();
        String usersex = spin_sex.getSelectedItem().toString().trim();

        db = openOrCreateDatabase("user_data", MODE_PRIVATE, null);
        ContentValues values = new ContentValues();
        values.put("user_name", username);
        values.put("user_telephone", usertelephone);
        values.put("user_age", userage);
        values.put("user_address", useraddress);
        values.put("user_email", useremail);
        values.put("user_sex", usersex);

        db.update("users_new", values, "_id=?", new String[]{ID});




//        info_exit_Name.setText(username);
//        info_exit_Telephone.setText(usertelephone);
//        info_exit_Age.setText(userage);
//        info_exit_Address.setText(useraddress);
//        info_exit_Email.setText(useremail);
//        info_exit_Sex.setText(usersex);

    }

    protected void onResume() {
        super.onResume();


    }
}
