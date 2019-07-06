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

/**
 * Created by Roelin on 2017/7/2.
 */

public class MonthNotesActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private SimpleAdapter listItemAdapter;
    private ListView list;
    private LinearLayout searchLinear,buttonLinear;
    private ArrayList<HashMap<String,Object>> listItem;
    private EditText editSearch;
    private Button serachButton;
    private String sql;
    private String userID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        setContentView(R.layout.detail_notes_layout);
        db = openOrCreateDatabase("money.db",MODE_PRIVATE,null);
        db.execSQL("create table if not exists moneytb (_id int primary key , money double , time time not null ,userID text not null, state text ,isDelete text)");
        editSearch = (EditText)findViewById(R.id.edit_search);
        serachButton = (Button)findViewById(R.id.search_btn);
        sql = "select * from moneytb where time like '%" + getTime()+"%' and isDelete = 'false' and userID = "+userID;
        showNote(sql);
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
                    sql = "select * from moneytb where time like '%" + getTime()+"%' and isDelete = 'false' and userID = "+userID;
                else
                    sql = "select * from moneytb where time like '%" + search+"%' or state like '%"+search+"%' and isDelete = 'false' and userID = "+userID;
                showNote(sql);
                editSearch.setText("");
                editSearch.setHint("请输入搜索内容(原搜索内容:"+ search+")");
                editSearch.setFocusable(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,Object> item = listItem.get(position);
                String selectedTitle = item.get("ItemTitle").toString();
                String selectedID = selectedTitle.substring(3);
                Intent intent = new Intent(MonthNotesActivity.this, EditNotes.class);
                intent.putExtra("ID",selectedID);
                startActivity(intent);

            }

        });

    }
    public void showNote(String sql) {
        list = (ListView)findViewById(R.id.notes_list);
        listItem = new ArrayList<HashMap<String,Object> >();
        Cursor c = db.rawQuery(sql, null);
        if(c!=null)
            while(c.moveToNext()) {
                HashMap<String,Object> map =new HashMap<String,Object>();
                Double money = c.getDouble(c.getColumnIndex("money"));
                String time = c.getString(c.getColumnIndex("time"));
                int picID = R.drawable.income;
                if(money<0) {
                    picID = R.drawable.shop;
                }
                int id = c.getInt(c.getColumnIndex("_id"));
                String title = "id:"+id;
                map.put("ItemPic", picID);
                map.put("ItemTitle", title);
                map.put("ItemTime", time);
                map.put("ItemMoney", money);
                listItem.add(map);
            }

        listItemAdapter = new SimpleAdapter(this, listItem,//数据源
                R.layout.note_item_layout,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"ItemPic", "ItemTitle", "ItemTime", "ItemMoney"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.note_pic, R.id.note_title, R.id.note_time, R.id.note_money}
        );

        if(c.getCount()!=0) {
            list.setVisibility(View.VISIBLE);
            list.setAdapter(listItemAdapter);
        }
        else
            list.setVisibility(View.GONE);
    }

    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sql = "select * from moneytb where time like '%" + getTime()+"%' and isDelete = 'false' and userID = "+userID;
        showNote(sql);
    }
}
