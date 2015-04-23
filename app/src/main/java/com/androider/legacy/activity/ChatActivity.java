package com.androider.legacy.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.service.NetService;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;


public class ChatActivity extends ActionBarActivity {

    MaterialEditText talkInput;
    AddFloatingActionButton talkSend;
    public static int seller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        seller = getIntent().getIntExtra("seller", -1);
        talkInput = (MaterialEditText)findViewById(R.id.chat_input);
        talkSend = (AddFloatingActionButton)findViewById(R.id.chat_send);
        talkSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talk();
            }
        });
    }

    public void talk(){
        Holder.myTalkConent = talkInput.getText().toString();
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.sendChat);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
