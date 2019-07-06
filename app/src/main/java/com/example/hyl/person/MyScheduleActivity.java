package com.example.hyl.person;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MyScheduleActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private SimpleAdapter daylistItemAdapter;
    private ListView daylist;
    private LinearLayout searchLinear,buttonLinear;
    private ArrayList<HashMap<String,Object>> daylistItem;
    private EditText editSearch;
    private Button serachButton;
    private String sql;
    private String userID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        setContentView(R.layout.detail_notes_layout);
        db = openOrCreateDatabase("schedule.db",MODE_PRIVATE,null);
        db.execSQL("create table if not exists scheduletb (_id INTEGER PRIMARY KEY AUTOINCREMENT, starttime time not null , endtime time not null ," +
                "userID text not null, title text not null, befores text not null ,location text , notes text , isClick text, beforetime text not null)");
        editSearch = (EditText)findViewById(R.id.edit_search);
        serachButton = (Button)findViewById(R.id.search_btn);
        sql = "select * from scheduletb where userID = "+userID;
        showSchedule(sql);
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearch.selectAll();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        serachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = editSearch.getText().toString();
                if(search.equals(""))
                    sql = "select * from scheduletb where userID = "+userID;
                else
                    sql = "select * from scheduletb where starttime like '%" + search+"%' or title like '%"+search+"%' or location like '%"+search+"%' or notes like '%"+search+"%' and userID = "+userID;
                showSchedule(sql);
                editSearch.setText("");
                editSearch.setHint("请输入搜索内容(原搜索内容:"+ search+")");
                editSearch.setFocusable(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });

        daylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,Object> item = daylistItem.get(position);
                String selectedTitle = item.get("ItemTitle").toString();
                String selected[] = selectedTitle.split(":");
                Intent intent =new Intent();
                intent.setClass(MyScheduleActivity.this,EditScheduleActivity.class);
                //Intent intent=new Intent(IntentTest.this,MyActivity.class);
                intent.putExtra("ScheduleID",selected[0]);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }
    public void showSchedule(String sql) {
        daylist = (ListView) findViewById(R.id.notes_list);
        daylistItem = new ArrayList<HashMap<String, Object>>();
        String[] title = {};
        int picID = R.drawable.to;
        Cursor c = db.rawQuery(sql, null);
        if (c != null)
            while (c.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", c.getString(c.getColumnIndex("_id"))+":"+c.getString(c.getColumnIndex("title")));
                map.put("ItemTime", c.getString(c.getColumnIndex("starttime")));
                map.put("ItemPic",picID);
                daylistItem.add(map);
            }



        daylistItemAdapter = new SimpleAdapter(this, daylistItem,//数据源
                R.layout.schedule_search_item_layout,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"ItemTitle", "ItemTime", "ItemPic"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.schedule_list_title, R.id.schedule_list_time, R.id.to}
        );
        daylist.setAdapter(daylistItemAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showSchedule(sql);
    }

}
