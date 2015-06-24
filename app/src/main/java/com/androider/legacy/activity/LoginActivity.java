package com.androider.legacy.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.User;
import com.androider.legacy.service.AccountService;
import com.androider.legacy.util.Encryption;
import com.androider.legacy.util.WatcherSimplifier;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    FloatingActionButton loginButton;
    MaterialEditText username;
    MaterialEditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSupportActionBar((Toolbar)findViewById(R.id.simple_toolbar));
        username = (MaterialEditText)findViewById(R.id.login_username);
        password = (MaterialEditText)findViewById(R.id.login_password);
        loginButton = (FloatingActionButton)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.instance.email = username.getText().toString();
                User.instance.password = Encryption.encrypt(password.getText().toString());
                login();
            }
        });
        WatcherSimplifier watcher = new WatcherSimplifier() {
            @Override
            public void afterTextChanged(Editable s) {
                if(username.getText().toString().equals(Constants.emptyString)||password.getText().toString().equals(Constants.emptyString))
                    loginButton.setEnabled(false);
                else
                    loginButton.setEnabled(true);
            }
        };
        username.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        loginButton.setEnabled(false);
    }

    private void login(){
        Intent intent = new Intent(this, AccountService.class);
        intent.putExtra(Constants.intentType, Constants.loginAttempt);
        startService(intent);
    }



}
