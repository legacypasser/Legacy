package com.androider.legacy.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.User;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.service.NetService;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends SimpleActivity {

    AddFloatingActionButton button;
    MaterialEditText email;
    MaterialEditText password;
    MaterialEditText nickname;
    MaterialEditText school;
    MaterialEditText major;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setToolBar();
        button = (AddFloatingActionButton)findViewById(R.id.register);
        email = (MaterialEditText)findViewById(R.id.email);
        password = (MaterialEditText)findViewById(R.id.password);
        nickname = (MaterialEditText)findViewById(R.id.nickname);
        school = (MaterialEditText)findViewById(R.id.school);
        major = (MaterialEditText)findViewById(R.id.major);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegistration();
            }
        });
    }

    public void sendRegistration(){
        User.email = email.getText().toString();
        User.password = password.getText().toString();
        User.nickname = nickname.getText().toString();
        User.school = school.getText().toString();
        User.major = major.getText().toString();
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.registrationSent);
        startService(intent);
    }


}
