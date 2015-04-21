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

public class RegisterActivity extends ActionBarActivity {

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
