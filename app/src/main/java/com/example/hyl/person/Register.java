package com.example.hyl.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    private String mSex;
    private EditText mID;
    private EditText mName;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private EditText mTelephone;
    private EditText mAge;
    private EditText mAddress;
    private EditText mEmail;
//    private EditText mSex;
    private RadioGroup sexgroup;
    private RadioButton rs_man;
    private RadioButton rs_woman;
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private UserDataManager mUserDataManager;         //用户数据管理类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mID = (EditText) findViewById((R.id.ResetPassword));
        mName = (EditText) findViewById(R.id.ResetPassName);
        mPwd = (EditText) findViewById(R.id.OldPassword);
        mPwdCheck = (EditText) findViewById(R.id.NewPassword);
        mTelephone = (EditText)findViewById(R.id.telephone);
        mAddress =(EditText)findViewById(R.id.address);
        mAge = (EditText)findViewById(R.id.Age);
        mEmail = (EditText)findViewById(R.id.email);
//        mSex = (EditText)findViewById(R.id.sex);

        rs_man = (RadioButton)findViewById(R.id.rs_man);
        rs_woman = (RadioButton) findViewById(R.id.rs_women);

        mSureButton = (Button) findViewById(R.id.Sure);
        mCancelButton = (Button) findViewById(R.id.Cancel);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_register_Listener);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();                              //建立本地数据库
        }
        sexgroup=(RadioGroup)findViewById(R.id.sex_select);
        sexgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if(checkedId==R.id.rs_man){
                    mSex = "男";
                }
                else {
                    mSex = "女";
                }
            }
        });
    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Sure:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.Cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(Register.this,LoginActivity.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };


    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String userID = mID.getText().toString().trim();
            String userName = mName.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();
            String telephone = mTelephone.getText().toString().trim();
            String Address = mAddress.getText().toString().trim();
            String Age = mAge.getText().toString().trim();
            String Email = mEmail.getText().toString().trim();
            String Sex = mSex;

            //检查用户是否存在
            int count=mUserDataManager.findUserByName(userID);
            //用户已经存在时返回，给出提示文字
            if(count>0){
                Toast.makeText(this, getString(R.string.name_already_exist, userID), Toast.LENGTH_SHORT).show();
                return ;
            }
            if(userPwd.equals(userPwdCheck)==false){     //两次密码输入不一样
                Toast.makeText(this, getString(R.string.pwd_not_the_same), Toast.LENGTH_SHORT).show();
                return ;
            } else {
                UserData mUser = new UserData(userID,userPwd,userName,telephone,Age,Address,Sex,Email);
                mUserDataManager.openDataBase();
                long flag = mUserDataManager.insertUserData(mUser); //新建用户信息
                if (flag == -1) {
                    Toast.makeText(this, getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    Intent intent_Register_to_Login = new Intent(Register.this,LoginActivity.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                }
            }
        }
    }
    public boolean isUserNameAndPwdValid() {
        if (mID.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.id_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mName.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mTelephone.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.telephone_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mAddress.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.address_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.email_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mAge.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.age_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}