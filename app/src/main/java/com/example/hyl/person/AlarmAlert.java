package com.example.hyl.person;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import java.text.ParseException;

public class AlarmAlert extends Activity {
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = openOrCreateDatabase("schedule.db", MODE_PRIVATE, null);
        db.execSQL("create table if not exists scheduletb (_id INTEGER PRIMARY KEY AUTOINCREMENT, starttime time not null , endtime time not null ," +
                "userID text not null, title text not null, repeat text not null , before text not null ,location text , notes text , isClick text , beforetime text not null)");

        new AlertDialog.Builder(AlarmAlert.this)
                .setIcon(R.drawable.clock)
                .setTitle(MainActivity.scheduleTitle)
                .setMessage(MainActivity.scheduleNotes)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.execSQL("update scheduletb set isClick = 'true' where _id = "+MainActivity.scheduleID);
                                System.exit(0);
                            }
                        }).show();
    }

}

