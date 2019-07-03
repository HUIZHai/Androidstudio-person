package com.example.hyl.person;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private Button btnlogin;
    private Button btnregister;
    private Button Third_party_login;
    private EditText Account_number;
    private EditText Password;
    private CheckBox RememberPassword;
    private SharedPreferences login_sp;

    private View loginView;
    private View loginSuccessView;
    private TextView loginSuccessShow;
    private UserDataManager mUserDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Account_number = (EditText)findViewById(R.id.Login_ID);
        Password = (EditText)findViewById(R.id.Password);

        loginView=findViewById(R.id.login_view);
        loginSuccessView=findViewById(R.id.login_success_view);
        loginSuccessShow=(TextView) findViewById(R.id.login_success_show);
        RememberPassword = (CheckBox) findViewById(R.id.RememberPassword);

        login_sp = getSharedPreferences("userInfo", 0);
        String name=login_sp.getString("USER_NAME", "");
        String pwd =login_sp.getString("PASSWORD", "");
        boolean choseRemember =login_sp.getBoolean("mRememberCheck", false);

        if(choseRemember){
            Account_number.setText(name);
            Password.setText(pwd);
            RememberPassword.setChecked(true);
        }

        btnregister = (Button)findViewById(R.id.register);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.register:
                        Log.d("MainActivity","1");
                        Intent Register = new Intent(LoginActivity.this,Register.class) ;
                        Log.d("MainActivity","5");
                        startActivity(Register);}
            }
        });


        btnlogin = (Button)findViewById(R.id.login);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.login:
                        login();}
            }
        });




        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
        image.setImageResource(R.drawable.touxiang);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();                              //建立本地数据库
        }
    }

    public void login() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String userID = Account_number.getText().toString().trim();    //获取当前输入的用户ID和密码信息
            String userPwd = Password.getText().toString().trim();
            SharedPreferences.Editor editor =login_sp.edit();
            int result=mUserDataManager.findUserByNameAndPwd(userID, userPwd);
            if(result==1){                                             //返回1说明用户名和密码均正确
                //保存用户名和密码
                editor.putString("USER_NAME", userID);
                editor.putString("PASSWORD", userPwd);

                //是否记住密码
                if(RememberPassword.isChecked()){
                    editor.putBoolean("mRememberCheck", true);
                }else{
                    editor.putBoolean("mRememberCheck", false);
                }
                editor.commit();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class) ;//切换Login Activity至User Activity
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
                Toast.makeText(this, getString(R.string.login_success),Toast.LENGTH_SHORT).show();//登录成功提示
            }else if(result==0){
                Toast.makeText(this, getString(R.string.login_fail),Toast.LENGTH_SHORT).show();  //登录失败提示
            }
        }
    }

    public boolean isUserNameAndPwdValid() {
        if (Account_number.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (Password.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    @Override
    protected void onResume() {
        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mUserDataManager != null) {
            mUserDataManager.closeDataBase();
            mUserDataManager = null;
        }
        super.onPause();
    }
}