package com.example.hyl.person;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Roelin on 2017/7/6.
 */

public class EditScheduleActivity  extends AppCompatActivity {
    private SQLiteDatabase db;
    private ImageButton editActBack,editActCheck,editActDelete;
    private EditText editActTitle,editActLoc,editActNotes;
    private ListView list;
    private ArrayList<HashMap<String, Object>> listItem;
    private SimpleAdapter listItemAdapter;
    private String userID;

    private String[] notes = {getDate(),getStartTime(),getDate(),getEndTime(),"一次性活动","10分钟前"};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_action_layout);
        Intent intent = getIntent();
        final String ScheduleID = intent.getStringExtra("ScheduleID");
        userID = intent.getStringExtra("userID");
        editActBack = (ImageButton)findViewById(R.id.back);
        editActCheck = (ImageButton)findViewById(R.id.check);
        editActDelete = (ImageButton)findViewById(R.id.delete) ;
        editActTitle = (EditText)findViewById(R.id.active_title);
        editActLoc = (EditText)findViewById(R.id.area);
        editActNotes = (EditText)findViewById(R.id.explain);
        list = (ListView)findViewById(R.id.day_schedule);
        db = openOrCreateDatabase("schedule.db", MODE_PRIVATE, null);
        db.execSQL("create table if not exists scheduletb (_id INTEGER PRIMARY KEY AUTOINCREMENT, starttime time not null , endtime time not null ," +
                "userID text not null, title text not null, repeat text not null,befores text not null ,location text , notes text , isClick text , beforetime text not null)");
        showDetails();
        String sql = "select * from scheduletb where _id = " + ScheduleID +" and userID = "+userID;
        Cursor c = db.rawQuery(sql, null);
        if(c!=null)
            if(c.moveToNext()) {
                String mStarttime = c.getString(c.getColumnIndex("starttime"));
                String mEndtime = c.getString(c.getColumnIndex("endtime"));
                String mTitle = c.getString(c.getColumnIndex("title"));
                String mBefore = c.getString(c.getColumnIndex("before"));
                String mRepeat = c.getString(c.getColumnIndex("repeat"));
                String mLocation = c.getString(c.getColumnIndex("location"));
                String mNotes = c.getString(c.getColumnIndex("notes"));
                String mStart[] = mStarttime.split(" ");
                String mEnd[] = mEndtime.split(" ");
                notes[0] = mStart[0];
                notes[1] = mStart[1];
                notes[2] = mEnd[0];
                notes[3] = mEnd[1];
                notes[4] = mRepeat;
                notes[5] = mBefore;
                editActTitle.setText(mTitle);
                editActLoc.setText(mLocation);
                editActNotes.setText(mNotes);

            }
        editActTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editActTitle.selectAll();
            }
        });
        editActLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editActLoc.selectAll();
            }
        });
        editActNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editActNotes.selectAll();
            }
        });
        editActCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTitle =editActTitle.getText().toString();
                String editLoc =editActLoc.getText().toString();
                String editNotes =editActNotes.getText().toString();
                if(editTitle.equals(""))
                    new AlertDialog.Builder(EditScheduleActivity.this)
                            .setIcon(R.drawable.warning)
                            .setTitle("Warning")
                            .setMessage("请输入活动标题")
                            .setPositiveButton("确定", null)
                            .show();
                else if(isEndBeforeStart(notes[0],notes[1],notes[2],notes[3]))
                    new AlertDialog.Builder(EditScheduleActivity.this)
                            .setIcon(R.drawable.warning)
                            .setTitle("Warning")
                            .setMessage("结束时间不能在开始时间之前")
                            .setPositiveButton("确定", null)
                            .show();
                else {
                    String set = null;
                    try {
                        set = "starttime= '" + notes[0]+" "+notes[1] + "', endtime='" +notes[2]+" "+notes[3] + "',repeat='" + notes[4] + "',before='" +notes[5]+"',title='" +editTitle+ "',location='" +editLoc+ "',notes='"+editNotes+"',beforetime='"+getBeforeTime( notes[0]+" "+notes[1],notes[5])+"',isClick = 'false'";
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    db.execSQL("update scheduletb set "+set+" where _id = " + ScheduleID +" and userID = "+userID);
                    AlarmListen();
                    Dialog progressDialog= new Dialog(EditScheduleActivity.this,R.style.progress_dialog);
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
            }



        });
        editActBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editActDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDelete(ScheduleID);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (position){
                    case 0:{
                        HashMap<String,Object> item = listItem.get(position);
                        String startDate = item.get("ItemNotes").toString();;
                        int year = Integer.valueOf(startDate.substring(0,4));
                        int month = Integer.valueOf(startDate.substring(5,7));
                        int day = Integer.valueOf(startDate.substring(8,10));
                        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                        new DatePickerDialog(EditScheduleActivity.this,
                                // 绑定监听器
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        int monthTemp = monthOfYear+1;
                                        String mMonth;
                                        if(monthTemp<10)
                                            mMonth = "0"+monthTemp;
                                        else
                                            mMonth=""+monthTemp;
                                        int dayTemp = dayOfMonth;
                                        String mDay;
                                        if(dayTemp<10)
                                            mDay = "0"+dayTemp;
                                        else
                                            mDay=""+dayTemp;
                                        notes[0]=year + "/" + mMonth + "/" + mDay ;
                                        onResume();
                                    }
                                }
                                // 设置初始日期
                                , year, month-1, day).show();

                    }break;
                    case 1:{
                        HashMap<String,Object> item = listItem.get(position);
                        String startTime = item.get("ItemNotes").toString();
                        final int hour = Integer.valueOf(startTime.substring(0,2));
                        int minutes = Integer.valueOf(startTime.substring(3,5));
                        new TimePickerDialog(EditScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int hourTemp = hourOfDay;
                                String mHour;
                                if(hourTemp<10)
                                    mHour = "0"+hourTemp;
                                else
                                    mHour=""+hourTemp;
                                int minTemp = minute;
                                String mMinutes;
                                if(minTemp<10)
                                    mMinutes = "0"+minTemp;
                                else
                                    mMinutes=""+minTemp;
                                notes[1]=mHour + ":" +mMinutes ;
                                onResume();
                            }
                        },hour,minutes, true).show();

                    }break;
                    case 2:{
                        HashMap<String,Object> item = listItem.get(position);
                        String endDate = item.get("ItemNotes").toString();;
                        int year = Integer.valueOf(endDate.substring(0,4));
                        int month = Integer.valueOf(endDate.substring(5,7));
                        int day = Integer.valueOf(endDate.substring(8,10));
                        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                        new DatePickerDialog(EditScheduleActivity.this,
                                // 绑定监听器
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        int monthTemp = monthOfYear+1;
                                        String mMonth;
                                        if(monthTemp<10)
                                            mMonth = "0"+monthTemp;
                                        else
                                            mMonth=""+monthTemp;
                                        int dayTemp = dayOfMonth;
                                        String mDay;
                                        if(dayTemp<10)
                                            mDay = "0"+dayTemp;
                                        else
                                            mDay=""+dayTemp;
                                        notes[2]=year + "/" + mMonth + "/" + mDay ;
                                        onResume();
                                    }
                                }
                                // 设置初始日期
                                , year, month-1, day).show();
                    }break;
                    case 3 :{
                        HashMap<String,Object> item = listItem.get(position);
                        String endTime = item.get("ItemNotes").toString();
                        final int hour = Integer.valueOf(endTime.substring(0,2));
                        int minutes = Integer.valueOf(endTime.substring(3,5));
                        new TimePickerDialog(EditScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int hourTemp = hourOfDay;
                                String mHour;
                                if(hourTemp<10)
                                    mHour = "0"+hourTemp;
                                else
                                    mHour=""+hourTemp;
                                int minTemp = minute;
                                String mMinutes;
                                if(minTemp<10)
                                    mMinutes = "0"+minTemp;
                                else
                                    mMinutes=""+minTemp;
                                notes[3]=mHour + ":" +mMinutes ;
                                onResume();
                            }
                        },hour,minutes, true).show();
                    }break;
                    case 4:{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(EditScheduleActivity.this);
                        builder.setTitle("重复");
                        final String[] choice = new String[]{"一次性活动", "每天","每月","每年"};
                        int num = 0;
                        for(int i = 0 ; i < 4 ; i++){
                            if(notes[4].equals(choice[i]))
                                num = i;
                        }
                        builder.setSingleChoiceItems(choice, num,    new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes[4]=choice[which];
                                dialog.dismiss(); // 让窗口消失
                                onResume();
                            }
                        });
                        builder.create().show();
                    }break;
                    default:{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(EditScheduleActivity.this);
                        builder.setTitle("提醒");
                        final String[] choice = new String[]{"立即响起","10分钟前", "30分钟前", "1小时前","2小时前","1天前"};
                        int num = 0;
                        for(int i = 0 ; i < 6 ; i++){
                            if(notes[5].equals(choice[i]))
                                num = i;
                        }
                        builder.setSingleChoiceItems(choice, num,    new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes[5]=choice[which];
                                dialog.dismiss(); // 让窗口消失
                                onResume();
                            }
                        });
                        builder.create().show();
                    }break;
                }
            }
        });

    }
    public void showDetails() {
        list = (ListView) findViewById(R.id.day_schedule_list);
        listItem = new ArrayList<HashMap<String, Object>>();
        String[] title = {"开始日期", "开始时间","结束日期","结束时间", "重        复","提        醒"};
        int picID = R.drawable.to;
        for (int i = 0; i < 6; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", title[i]);
            map.put("ItemNotes", notes[i]);
            map.put("ItemPic",picID);
            listItem.add(map);
        }


        listItemAdapter = new SimpleAdapter(this, listItem,//数据源
                R.layout.active_item_layout,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"ItemTitle", "ItemNotes", "ItemPic"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.act_title, R.id.act_time, R.id.to}
        );
        list.setAdapter(listItemAdapter);

    }
    private String getStartTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }
    private String getEndTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date curDate = new Date();
        Date endDate = new Date();
        endDate.setTime(curDate.getTime()+1000*60*60);
        String str = format.format(endDate);
        return str;
    }
    private void AlarmListen() {
        String sql = "select * from scheduletb where beforetime  >= '" + getCurrentTime() + "' and isClick = 'false' and userID = " + userID +"  order by beforetime asc";
        Cursor c = db.rawQuery(sql, null);
        if (c != null) {
            int i = 0;
            if (c.moveToNext()) {
                MainActivity.scheduleID  = c.getInt(c.getColumnIndex("_id"));
                MainActivity.scheduleNotes = c.getString(c.getColumnIndex("notes"));
                MainActivity.scheduleTime = c.getString(c.getColumnIndex("beforetime"));
                MainActivity.scheduleTitle = c.getString(c.getColumnIndex("title"));
            }
        }
        if(MainActivity.scheduleTime!=null) {
            try {
                MainActivity.mCalendar = MainActivity.getStartCalandar(MainActivity.scheduleTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            MainActivity.mAlarmHelper.openAlarm(32, MainActivity.scheduleTitle, MainActivity.scheduleNotes, MainActivity.mCalendar.getTimeInMillis());
        }
    }
    private String getCurrentTime() {
        SimpleDateFormat format= new SimpleDateFormat("yyyy/MM/dd HH:ss");;
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }
    private boolean isEndBeforeStart(String startDate , String startTime ,String endDate , String endTime){
        int startYear , startMonth , startDay , startHour , startMinutes;
        int endYear , endMonth , endDay , endHour , endMinutes;
        startYear = Integer.valueOf(startDate.substring(0,4));
        startMonth = Integer.valueOf(startDate.substring(5,7));
        startDay = Integer.valueOf(startDate.substring(8,10));
        startHour = Integer.valueOf(startTime.substring(0,2));
        startMinutes = Integer.valueOf(startTime.substring(3,5));
        endYear = Integer.valueOf(endDate.substring(0,4));
        endMonth = Integer.valueOf(endDate.substring(5,7));
        endDay = Integer.valueOf(endDate.substring(8,10));
        endHour = Integer.valueOf(endTime.substring(0,2));
        endMinutes = Integer.valueOf(endTime.substring(3,5));
        if(startYear>endYear)
            return true;
        else if (startYear<endYear){
            return false;
        }
        else {
            if (startMonth>endMonth)
                return true;
            else if (startMonth<endMonth)
                return false;
            else {
                if (startDay>endDay)
                    return true;
                else if (startDay<endDay)
                    return false;
                else {
                    if (startHour>endHour)
                        return true;
                    else if (startHour<endHour)
                        return false;
                    else {
                        if (startMinutes>endMinutes)
                            return true;
                        else
                            return false;
                    }
                }
            }
        }
    }
    private String getBeforeTime(String startTime , String before) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date beforeDate = new Date();
        String str ;
        switch (before){
            case "立即响起":{
                beforeDate.setTime(getDate(startTime).getTime());
                str = format.format(beforeDate);
            }break;
            case "10分钟前":{
                beforeDate.setTime(getDate(startTime).getTime()-1000*10*60);
                str = format.format(beforeDate);
            }break;
            case "30分钟前":{
                beforeDate.setTime(getDate(startTime).getTime()-1000*30*60);
                str = format.format(beforeDate);
            }break;
            case "1小时前":{
                beforeDate.setTime(getDate(startTime).getTime()-1000*60*60);
                str = format.format(beforeDate);
            }break;
            case "2小时前":{
                beforeDate.setTime(getDate(startTime).getTime()-1000*2*60*60);
                str = format.format(beforeDate);
            }break;
            default:{
                beforeDate.setTime(getDate(startTime).getTime()-1000*24*10*60);
                str = format.format(beforeDate);
            }break;
        }
        return str;
    }
    public static Date getDate(String str) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date= sdf.parse(str);
        return date;
    }
    private void isDelete(String id){
        final String mID = id;
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("删除")
                .setMessage("确认删除?")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("delete from scheduletb where _id = " + mID + " and userID = "+userID );
                                Dialog progressDialog= new Dialog(EditScheduleActivity.this,R.style.progress_dialog);
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
        android.app.AlertDialog ad = builder.create();
        ad.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        showDetails();
    }
}


