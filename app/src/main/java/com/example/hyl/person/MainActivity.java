package com.example.hyl.person;

//import android.app.Dialog;
//import android.content.Intent;
//import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;
//import android.icu.math.BigDecimal;
//import android.os.Build;
import android.os.Bundle;
//import android.os.Process;
import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
//import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.view.MenuItem;
import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import ArcProcess.ArcProgress;

public class MainActivity extends AppCompatActivity {
    private Button makeNotes;
    private LinearLayout content1, content3;
    private CoordinatorLayout content2;
    private ListView list;
    private ArrayList<HashMap<String, Object>> listItem;
    private SimpleAdapter listItemAdapter;
    private ListView daylist;
    private ArrayList<HashMap<String, Object>> daylistItem;
    private SimpleAdapter daylistItemAdapter;
    private SQLiteDatabase db, db2, db3;
    private TextView mIncome, mOutcome, mBalance, currentMonth;
//    private FloatingActionButton addActicity;
//    private CalendarView calView;
//    private String selectDate = getTime(0);
    private TextView dayInfo;
    TextView mylife_name;
    TextView btn_infomation;
    TextView btn_Schedule;
    TextView btn_code;
    private String userID;
    ImageButton mylife_settings;
//    private ArcProgress moneyPro;
//    public static AlarmHelper mAlarmHelper;
//    public static Calendar mCalendar;
//    public static int scheduleID;
//    public static String scheduleNotes, scheduleTime, scheduleTitle;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_money:
                    content1.setVisibility(View.VISIBLE);
                    content2.setVisibility(View.GONE);
                    content3.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_calendar:
                    content1.setVisibility(View.GONE);
                    content2.setVisibility(View.VISIBLE);
                    content3.setVisibility(View.GONE);
                    calView.setDate(new Date(getTime(0)).getTime());
                    if (hasSchedule(getTime(0))) {
                        dayInfo.setText("今日日程");
                        daylist.setVisibility(View.VISIBLE);
                        showSchedule(getTime(0));
                    } else {
                        dayInfo.setText("今日没有日程");
                        daylist.setVisibility(View.GONE);
                    }
                    return true;
                case R.id.navigation_user:
                    content1.setVisibility(View.GONE);
                    content2.setVisibility(View.GONE);
                    content3.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }
    }
}
