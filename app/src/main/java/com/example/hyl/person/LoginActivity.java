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

public class LoginActivity extends AppCompatActivity{
    private Button btnlogin;
    private Button btnregister;
    private EditText Account_number;
    private EditText Password;
    private CheckBox RememberPassword;
    private SharedPreferences login_sp;

    private View loginView;
    private View loginSuccessView;
    private TextView loginSuccessShow;


}
