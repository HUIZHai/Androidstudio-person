package com.example.hyl.person;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.math.BigDecimal;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ArcProcess.ArcProgress;

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
    private FloatingActionButton addActicity;
    private CalendarView calView;
   
    private String selectDate = getTime(0);
    private TextView dayInfo;
    TextView mylife_name;
    TextView btn_infomation;
    TextView btn_Schedule;
    TextView btn_code;
    private String userID;
    ImageButton mylife_settings;
    private ArcProgress moneyPro;
    public static AlarmHelper mAlarmHelper;
    public static Calendar mCalendar;
    public static int scheduleID ;
    public static String scheduleNotes,scheduleTime,scheduleTitle;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
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
        mAlarmHelper =new AlarmHelper(this);
        db=openOrCreateDatabase("money.db",MODE_PRIVATE,null);
        db.execSQL("create table if not exists moneytb (_id int primary key , money double , time time not null ,userID text not null, state text ,isDelete text)");
        db2 = openOrCreateDatabase("schedule.db", MODE_PRIVATE, null);
        db2.execSQL("create table if not exists scheduletb (_id INTEGER PRIMARY KEY AUTOINCREMENT, starttime time not null , endtime time not null ," +
                "userID text not null, title text not null, repeat text not null,befores text not null ,location text , notes text , isClick text , beforetime text not null)");
        content1 = (LinearLayout) findViewById(R.id.content1);
        content2 = (CoordinatorLayout) findViewById(R.id.content2);
        content3 = (LinearLayout) findViewById(R.id.content3);
        makeNotes = (Button) findViewById(R.id.makeNotes);
        mIncome = (TextView) findViewById(R.id.income);
        mOutcome = (TextView) findViewById(R.id.outcome);
        mBalance = (TextView) findViewById(R.id.balance);
        currentMonth = (TextView) findViewById(R.id.currentMonth);
        addActicity = (FloatingActionButton) findViewById(R.id.fab);
        list = (ListView) findViewById(R.id.in_outcome_list);
        daylist = (ListView) findViewById(R.id.day_schedule);
        calView = (CalendarView) findViewById(R.id.calendarView);
        dayInfo = (TextView) findViewById(R.id.schedule_info);
        moneyPro = (ArcProgress)findViewById(R.id.moneyprocess);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_user);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (hasSchedule(selectDate)) {
            dayInfo.setText("今日日程");
            daylist.setVisibility(View.VISIBLE);
            showSchedule(selectDate);
        } else {
            dayInfo.setText("今日没有日程");
            daylist.setVisibility(View.GONE);
        }

        //日程
        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int monthTemp = month + 1;
                String selectMonth;
                if (monthTemp < 10)
                    selectMonth = "0" + monthTemp;
                else
                    selectMonth = "" + monthTemp;
                String selectDay;
                if (dayOfMonth < 10)
                    selectDay = "0" + dayOfMonth;
                else
                    selectDay = "" + dayOfMonth;
                selectDate = year + "/" + selectMonth + "/" + selectDay;
                daylist = (ListView) findViewById(R.id.day_schedule);
                dayInfo = (TextView) findViewById(R.id.schedule_info);
                if (hasSchedule(selectDate)) {
                    dayInfo.setText("今日日程");
                    daylist.setVisibility(View.VISIBLE);
                    showSchedule(selectDate);
                } else {
                    dayInfo.setText("今日没有日程");
                    daylist.setVisibility(View.GONE);
                }
            }
        });


        daylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> item = daylistItem.get(position);
                String selectedTitle = item.get("ItemTitle").toString();
                String selected[] = selectedTitle.split(":");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EditScheduleActivity.class);
                //Intent intent=new Intent(IntentTest.this,MyActivity.class);
                intent.putExtra("ScheduleID", selected[0]);
                intent.putExtra("userID", userID);
                startActivity(intent);
                onResume();
            }
        });

        addActicity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NewActActivity.class);
                //Intent intent=new Intent(IntentTest.this,MyActivity.class);
                intent.putExtra("selectDate", selectDate);
                intent.putExtra("userID", userID);
                startActivity(intent);
                // onResume();

            }
        });
    }


    public boolean hasSchedule(String date) {
        String sql = "select * from scheduletb where starttime  like '%" + date + "%'  and userID = " + userID;
        Cursor c = db2.rawQuery(sql, null);
        if (c != null) {
            if (c.moveToNext())
                return true;
            else{
                sql = "select * from scheduletb where repeat = '每天' and userID = " + userID;
                c = db2.rawQuery(sql, null);
                if (c != null) {
                    if (c.moveToNext())
                        return true;
                    else{
                        sql = "select * from scheduletb where starttime  like '%" + date.substring(7,10)+" " + "%' and repeat = '每月' and userID = " + userID ;
                        c = db2.rawQuery(sql, null);
                        if (c != null) {
                            if (c.moveToNext())
                                return true;
                            else {
                                sql = "select * from scheduletb where starttime  like '%" + date.substring(4,10) + "%' and repeat = '每年' and userID = " + userID  ;
                                c = db2.rawQuery(sql, null);
                                if (c != null) {
                                    if (c.moveToNext())
                                        return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    public void showSchedule(String selectedDate) {
        if (hasSchedule(selectedDate)) {
            dayInfo.setText("今日日程");
            daylist.setVisibility(View.VISIBLE);
        } else {
            dayInfo.setText("今日没有日程");
            daylist.setVisibility(View.GONE);
        }
        String yearRepeat = selectedDate.substring(4,10)+" ";
        String monthRepeat = selectedDate.substring(7,10)+" ";
        daylist = (ListView) findViewById(R.id.day_schedule);
        daylistItem = new ArrayList<HashMap<String, Object>>();
        String[] title = {};
        int picID = R.drawable.to;
        String sql = "select * from scheduletb where starttime  like '%" + selectedDate + "%' and repeat = '一次性活动' and userID = " + userID + " order by starttime asc";
        Cursor c = db2.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", c.getString(c.getColumnIndex("_id")) + ":" + c.getString(c.getColumnIndex("title")));
                map.put("ItemTime", c.getString(c.getColumnIndex("starttime")));
                map.put("ItemPic", picID);
                daylistItem.add(map);
            }
        sql = "select * from scheduletb where repeat = '每天' and userID = " + userID + " order by starttime asc";
        c = db2.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", c.getString(c.getColumnIndex("_id")) + ":" + c.getString(c.getColumnIndex("title")));
                map.put("ItemTime", c.getString(c.getColumnIndex("starttime")));
                map.put("ItemPic", picID);
                daylistItem.add(map);
            }
        sql = "select * from scheduletb where starttime  like '%" + monthRepeat + "%' and repeat = '每月' and userID = " + userID + " order by starttime asc";
        c = db2.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", c.getString(c.getColumnIndex("_id")) + ":" + c.getString(c.getColumnIndex("title")));
                map.put("ItemTime", c.getString(c.getColumnIndex("starttime")));
                map.put("ItemPic", picID);
                daylistItem.add(map);
            }
        sql = "select * from scheduletb where starttime  like '%" + yearRepeat + "%' and repeat = '每年' and userID = " + userID + " order by starttime asc";
        c = db2.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", c.getString(c.getColumnIndex("_id")) + ":" + c.getString(c.getColumnIndex("title")));
                map.put("ItemTime", c.getString(c.getColumnIndex("starttime")));
                map.put("ItemPic", picID);
                daylistItem.add(map);
            }

        daylistItemAdapter = new SimpleAdapter(this, daylistItem,//数据源
                R.layout.schedule_item_layout,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"ItemTitle", "ItemTime", "ItemPic"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.schedule_list_title, R.id.schedule_list_time, R.id.to}
        );
        daylist.setAdapter(daylistItemAdapter);

    }

    
    private String getTime(int i) {
        SimpleDateFormat format;
        if (i == 0)
            format = new SimpleDateFormat("yyyy/MM/dd");
        else if (i == 1)
            format = new SimpleDateFormat("yyyy/MM");
        else
            format = new SimpleDateFormat("yyyy");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }


}
