package com.androider.legacy.activity;

import android.content.Intent;
import android.hardware.usb.UsbRequest;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Nicker;
import com.androider.legacy.data.User;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.Locator;
import com.gc.materialdesign.views.ButtonFloat;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton;

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

        TextWatcher validator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!email.getText().toString().equals("")
                    &&!password.getText().toString().equals("")
                        &&!nickname.getText().toString().equals("")
                        &&!school.getText().toString().equals("")
                        &&!major.getText().toString().equals("")){
                    button.setEnabled(true);
                }else{
                    button.setEnabled(false);
                }

            }
        };

        email.addTextChangedListener(validator);
        password.addTextChangedListener(validator);
        nickname.addTextChangedListener(validator);
        school.addTextChangedListener(validator);
        major.addTextChangedListener(validator);
        nickname.setText(Nicker.getAdj() + Nicker.getNoun());
        button.setEnabled(false);
    }

    public void sendRegistration(){
        User.email = email.getText().toString();
        User.password = password.getText().toString();
        User.nickname = nickname.getText().toString();
        User.school = school.getText().toString();
        User.major = major.getText().toString();
        Location location = Locator.getLocation(this);
        if(location == null){
            User.lati = 0;
            User.longi = 0;
        }else {
            User.lati = location.getLatitude();
            User.longi = location.getLongitude();
        }
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.registrationSent);
        startService(intent);
        finish();
    }


}
