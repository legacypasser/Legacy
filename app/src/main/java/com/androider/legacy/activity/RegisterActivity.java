package com.androider.legacy.activity;

import android.content.Intent;
import android.hardware.usb.UsbRequest;
import android.location.Location;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.adapter.ChooseAdapter;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Nicker;
import com.androider.legacy.data.RequestData;
import com.androider.legacy.data.School;
import com.androider.legacy.data.User;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.DividerDecorator;
import com.androider.legacy.util.Encryption;
import com.androider.legacy.util.WatcherSimplifier;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    AddFloatingActionButton button;
    MaterialEditText email;
    MaterialEditText password;
    MaterialEditText nickname;
    MaterialEditText school;
    MaterialEditText major;
    School choosedSchool;
    EditText search;
    RecyclerView list;
    View.OnClickListener listener;
    LinearLayout chooser;

    WatcherSimplifier validator = new WatcherSimplifier() {
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
        search = (MaterialEditText)findViewById(R.id.pre_match);
        list = (RecyclerView)findViewById(R.id.choose_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerDecorator());
        chooser = (LinearLayout)findViewById(R.id.register_chooser);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegistration();
            }
        });
        listener = this;
        Toolbar toolbar = (Toolbar)findViewById(R.id.simple_toolbar);
        setSupportActionBar(toolbar);
        email.addTextChangedListener(validator);
        password.addTextChangedListener(validator);
        nickname.addTextChangedListener(validator);
        school.addTextChangedListener(validator);
        major.addTextChangedListener(validator);
        nickname.setText(Nicker.getAdj() + Nicker.getNoun());
        button.setEnabled(false);
        StateController.change(Constants.registerState);
        setToChooseSchool();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.random_name:
                nickname.setText(Nicker.getAdj() + Nicker.getNoun());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    WatcherSimplifier schoolWatcher = new WatcherSimplifier() {
        @Override
        public void afterTextChanged(Editable s) {
            list.setAdapter(new ChooseAdapter(School.prefixed(s.toString()), listener));
        }
    };

    WatcherSimplifier majorWatcher = new WatcherSimplifier() {
        @Override
        public void afterTextChanged(Editable s) {
            list.setAdapter(new ChooseAdapter(choosedSchool.prefixedMajors(s.toString()), listener));
        }
    };

    private void setToChooseSchool(){
        if(StateController.getCurrent() == Constants.registerState){
            StateController.change(Constants.schoolChoosing);
        }else if(StateController.getCurrent() == Constants.majorChoosing){
            StateController.goBack();
            StateController.change(Constants.schoolChoosing);
            search.removeTextChangedListener(majorWatcher);
        }
        search.setHint(getString(R.string.school_name));
        search.addTextChangedListener(schoolWatcher);
        if(User.instance.province == null)
            User.instance.positionUser(RequestData.fromBase(Constants.baiduUrl));
        list.setAdapter(new ChooseAdapter(School.regional(User.instance.province), this));
    }

    private void setToChooseMajor(){
        if(StateController.getCurrent() == Constants.registerState){
            StateController.change(Constants.schoolChoosing);
        }else if(StateController.getCurrent() == Constants.schoolChoosing){
            StateController.goBack();
            StateController.change(Constants.majorChoosing);
            search.removeTextChangedListener(schoolWatcher);
        }
        search.setHint(getString(R.string.major_name));
        search.addTextChangedListener(majorWatcher);
        list.setAdapter(new ChooseAdapter(choosedSchool.getMajors(), this));
    }

    public void sendRegistration(){
        User.instance.email = email.getText().toString();
        User.instance.password = Encryption.encrypt(password.getText().toString());
        User.instance.nickname = nickname.getText().toString();
        User.instance.school = school.getText().toString();
        User.instance.major = major.getText().toString();
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.registrationSent);
        startService(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        while (StateController.getCurrent() != Constants.mainState)
            StateController.goBack();
    }

    @Override
    public void onClick(View v) {
        if(StateController.getCurrent() == Constants.schoolChoosing){
            choosedSchool = School.maybeUsed.get(((TextView) v).getText().toString());
            school.setText(choosedSchool.name);
            setToChooseMajor();
        }else if(StateController.getCurrent() == Constants.majorChoosing){
            major.setText(((TextView) v).getText().toString());
            StateController.goBack();
            chooser.setVisibility(View.GONE);
        }
    }
}
