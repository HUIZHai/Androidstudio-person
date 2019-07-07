package com.example.hyl.person;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.math.BigDecimal;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.net.Uri;
import java.io.File;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import imgutils.SpUtils;
import imgutils.FileProvider7;
import imgutils.PermissionsChecker;
import imgutils.Utils;

import static imgutils.Utils.DIR_IMAGE;
import static imgutils.Utils.PERMISSIONS_STORAGE;
import static imgutils.Utils.REQUEST_EXTERNAL_STORAGE;
import static imgutils.Utils.SP_TEMP1;
import static imgutils.Utils.startPhotoZoom;

import android.provider.MediaStore;
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
    ImageView mylife_touxiang;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;

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

        AlarmListen();
        //记一笔
        makeNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MakeNotes.class);
                intent.putExtra("userID", userID);
                startActivity(intent);

            }
        });
        //今天、本月、本年记账
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        final Dialog progressDialog= new Dialog(MainActivity.this,R.style.progress_dialog);
                        progressDialog.setContentView(R.layout.load_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
                        msg.setText("疯狂加载中...");
                        progressDialog.show();
                        TimerTask finishTask = new TimerTask(){
                            public void run(){
                                //execute the task
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, DayNotesActivity.class);
                                //Intent intent=new Intent(IntentTest.this,MyActivity.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(finishTask , 2000);

                    }
                    break;
                    case 1: {
                        final Dialog progressDialog= new Dialog(MainActivity.this,R.style.progress_dialog);
                        progressDialog.setContentView(R.layout.load_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
                        msg.setText("疯狂加载中...");
                        progressDialog.show();
                        TimerTask finishTask = new TimerTask(){
                            public void run(){
                                //execute the task
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, MonthNotesActivity.class);
                                //Intent intent=new Intent(IntentTest.this,MyActivity.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(finishTask , 2000);

                    }
                    break;
                    default: {final Dialog progressDialog= new Dialog(MainActivity.this,R.style.progress_dialog);
                        progressDialog.setContentView(R.layout.load_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
                        msg.setText("疯狂加载中...");
                        progressDialog.show();
                        TimerTask finishTask = new TimerTask(){
                            public void run(){
                                //execute the task

                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, YearNotesActivity.class);
                                //Intent intent=new Intent(IntentTest.this,MyActivity.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(finishTask , 2000);

                    }
                    break;
                }
            }
        });


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




        addActicity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NewActActivity.class);
//                Intent intent=new Intent(IntentTest.this,MyActivity.class);
                intent.putExtra("selectDate", selectDate);
                intent.putExtra("userID", userID);
                startActivity(intent);
                // onResume();

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


        setMyName();
        btn_infomation = (TextView) findViewById(R.id.mylife_info);
        btn_infomation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("userID", userID);
                intent.setClass(MainActivity.this, InfomationActivity.class);
                startActivity(intent);
                onResume();
            }

        });
        btn_Schedule = (TextView) findViewById(R.id.mylife_day);
        btn_Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, MyScheduleActivity.class);
                intent1.putExtra("userID", userID);
                startActivity(intent1);
            }
        });
        mylife_touxiang=(ImageView) findViewById(R.id.mylife_touxiang);
        mylife_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionsChecker mPermissionsChecker = new PermissionsChecker(MainActivity.this);
                // 缺少权限时, 进入权限配置页面
                if (mPermissionsChecker.lacksPermissions(PERMISSIONS_STORAGE)) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE);
                } else {
                    showChoosePicDialog();
                }

            }

        });

        mylife_settings = (ImageButton) findViewById(R.id.mylife_settings);
        mylife_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("userID", userID);
                intent.setClass(MainActivity.this, settings.class);
                startActivity(intent);
                onResume();
            }

        });

        btn_code = (TextView) findViewById(R.id.mylife_code);
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(MainActivity.this, calc_activity.class);
                startActivity(intent2);
            }
        });

    }

    private void AlarmListen() {
        String sql = "select * from scheduletb where beforetime  >= '" + getCurrentTime() + "' and isClick = 'false' and userID = " + userID +"  order by beforetime asc";
        Cursor c = db2.rawQuery(sql, null);
        if (c != null) {
            int i = 0;
            if (c.moveToNext()) {
                scheduleID  = c.getInt(c.getColumnIndex("_id"));
                scheduleNotes = c.getString(c.getColumnIndex("notes"));
                scheduleTime = c.getString(c.getColumnIndex("beforetime"));
                scheduleTitle = c.getString(c.getColumnIndex("title"));
            }
        }
        if(scheduleTime!=null) {
            try {
                mCalendar = getStartCalandar(scheduleTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mAlarmHelper.openAlarm(32, scheduleTitle, scheduleNotes, mCalendar.getTimeInMillis());
        }
    }

    public void showDetails() {
        list = (ListView) findViewById(R.id.in_outcome_list);
        listItem = new ArrayList<HashMap<String, Object>>();
        String[] title = {"今天", "本月", "本年"};
        String[] time = {getTime(0), getTime(1), getTime(2)};
        int picID[] = {R.drawable.day, R.drawable.month, R.drawable.year};
        double money[] = {0.00, 0.00, 0.00};
        for (int i = 0; i < 3; i++) {
            String sql = "select * from moneytb where time  like '%" + getTime(i) + "%' and isDelete = 'false' and userID = " + userID;
            Cursor c = db.rawQuery(sql, null);
            if (c != null)
                while (c.moveToNext()) {
                    money[i] += c.getDouble(c.getColumnIndex("money"));
                }
        }
        for (int i = 0; i < 3; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemPic", picID[i]);
            map.put("ItemTitle", title[i]);
            map.put("ItemTime", time[i]);
            map.put("ItemMoney", String.format("%.2f", money[i]));
            listItem.add(map);
        }


        listItemAdapter = new SimpleAdapter(this, listItem,//数据源
                R.layout.note_detail_item_layout,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"ItemPic", "ItemTitle", "ItemTime", "ItemMoney"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.note_pic, R.id.note_title, R.id.note_time, R.id.note_money}
        );
        list.setAdapter(listItemAdapter);

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

    public void showMonthDetails() {
        currentMonth.setText(getMonth() + "月");
        //查询并设置本月收入
        double income = 0, outcome = 0, balance = 0;
        String sql = "select * from moneytb where time  like '%" + getTime(1) + "%' and isDelete = 'false' and money > 0 and userID = " + userID;
        Cursor c = db.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                income += c.getDouble(c.getColumnIndex("money"));
            }
        mIncome.setText(String.format("%.2f", income));
        //查询并设置本月支出
        sql = "select * from moneytb where time  like '%" + getTime(1) + "%' and isDelete = 'false' and money < 0 and userID = " + userID;
        c = db.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                outcome += c.getDouble(c.getColumnIndex("money"));
            }
        mOutcome.setText(String.format("%.2f", -1 * outcome));
        //查询并设置本月余额（收入-支出）
        sql = "select * from moneytb where time  like '%" + getTime(1) + "%' and isDelete = 'false' and userID = " + userID;
        c = db.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                balance += c.getDouble(c.getColumnIndex("money"));
            }
        mBalance.setText(String.format("%.2f", balance));
        if(balance>0) {
            moneyPro.setProgress((int) (-100 * outcome / income));
        }
        else
            moneyPro.setProgress(100);
    }

    protected void onResume() {
        super.onResume();
        showMonthDetails();
        showDetails();
        showSchedule(selectDate);
        setMyName();
        AlarmListen();
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

    private String getCurrentTime() {
        SimpleDateFormat format= new SimpleDateFormat("yyyy/MM/dd HH:ss");;
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }
    public static Calendar getStartCalandar(String str) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date= sdf.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private String getMonth() {
        SimpleDateFormat format;
        format = new SimpleDateFormat("MM");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

    private void setMyName() {
        db3 = openOrCreateDatabase("user_data", MODE_PRIVATE, null);
        String sql = "select * from users_new where _id = '" + userID + "'";
        Cursor cursor = db3.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            String username = cursor.getString(cursor.getColumnIndex("user_name"));
            mylife_name = (TextView) findViewById(R.id.mylife_name);
            mylife_name.setText(username);
        }
    }


    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_PICK);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(DIR_IMAGE, "image.jpg"));//Environment.getExternalStorageDirectory()
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    if (tempUri == null) {
                        Log.i("tag", "The uri is not exist.");
                    }
//                    tempUri = uri;
//                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    startActivityForResult(startPhotoZoom(tempUri), CROP_SMALL_PICTURE);
                    break;
                case CHOOSE_PICTURE:
//                    ActivityCompat.requestPermissions($.getActivity(),
//                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                    if (data.getData() == null) {
                        Log.i("tag", "The uri is not exist.");
                    }
                    tempUri = data.getData();
//                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    startActivityForResult(startPhotoZoom(data.getData()), CROP_SMALL_PICTURE);
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }


    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     * @param data
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = Utils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            mylife_touxiang.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        String imagePath = Utils.savePhoto(bitmap, DIR_IMAGE, String
                .valueOf("geeklovewho"));//System.currentTimeMillis()
        Log.e("imagePath", imagePath + "");
        if (imagePath != null) {
            // 拿着imagePath上传了
            // ...
            SpUtils.get(this).put(SP_TEMP1, imagePath);
        }
    }

}
