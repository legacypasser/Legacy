package com.androider.legacy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.Session;
import com.androider.legacy.data.User;
import com.androider.legacy.net.NetConstants;
import com.androider.legacy.service.ChatService;
import com.androider.legacy.util.WatcherSimplifier;
import com.jialin.chat.Message;
import com.jialin.chat.MessageAdapter;
import com.jialin.chat.MessageInputToolBox;
import com.jialin.chat.OnOperationListener;
import com.jialin.chat.Option;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;


public class ChatActivity extends AppCompatActivity{
    Session currentSession;
    MessageAdapter adapter;
    ArrayList<Message> messages = new ArrayList<>();
    ListView list;
    Button send;
    EditText input;
    public static NetHandler netHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        currentSession = (Session)getIntent().getSerializableExtra(Constants.chat);
        Toolbar toolbar = (Toolbar)findViewById(R.id.simple_toolbar);
        toolbar.setTitle(currentSession.nickname);
        setSupportActionBar(toolbar);
        netHandler = new NetHandler(this);
        input = (EditText)findViewById(R.id.chat_input);
        send = (Button)findViewById(R.id.chat_send);
        send.setEnabled(false);
        input.addTextChangedListener(new WatcherSimplifier() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(""))
                    send.setEnabled(false);
                else
                    send.setEnabled(true);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOnline(input.getText().toString());
                input.setText("");
            }
        });
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    hideSoft();
            }
        });
        initList();
    }

    private void hideSoft(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
    }

    private void initList(){
        list = (ListView) findViewById(R.id.message_list);
        ArrayList<Record> existsRecord = currentSession.getRecords();
        for(Record item : existsRecord){
            messages.add(item.formMessage());
        }
        adapter = new MessageAdapter(this, messages);
        list.setAdapter(adapter);
        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                input.clearFocus();
                return false;
            }
        });
        list.setSelection(list.getBottom());
    }

    private void sendOnline(String content){
        if(User.instance.alreadLogin){
            Intent intent = new Intent(this, ChatService.class);
            intent.putExtra(Constants.intentType, NetConstants.sendChat);
            intent.putExtra(Constants.id, currentSession.peer);
            intent.putExtra(NetConstants.content, content);
            startService(intent);
        }else
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
    }

    private void updateSession(Record record){
        if(record.getPeerId() == currentSession.peer){
            adapter.getData().add(record.formMessage());
            list.setSelection(list.getBottom());
        }
    }


    private static class NetHandler extends Handler {
        ChatActivity instance;
        NetHandler(ChatActivity chatActivity){
            instance = chatActivity;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Record record = (Record)msg.getData().getSerializable(Constants.chat);
            instance.updateSession(record);
        }
    }

}
