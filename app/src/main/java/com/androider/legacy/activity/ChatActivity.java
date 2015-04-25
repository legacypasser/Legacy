package com.androider.legacy.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.Session;
import com.androider.legacy.data.User;
import com.androider.legacy.service.NetService;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.jialin.chat.Message;
import com.jialin.chat.MessageAdapter;
import com.jialin.chat.MessageInputToolBox;
import com.jialin.chat.OnOperationListener;
import com.jialin.chat.Option;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ChatActivity extends SimpleActivity {

    public int talker;
    public Session currentSession;
    public static ChatActivity instance;
    MessageInputToolBox box;
    MessageAdapter adapter;
    HashMap<Integer, ArrayList<String>> faceData = new HashMap<>();
    ArrayList<Option> functionData = new ArrayList<>();
    ArrayList<String> faceNameList = new ArrayList<>();
    ArrayList<Message> messages = new ArrayList<>();
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        instance = this;
        talker = getIntent().getIntExtra("talker", -1);
        currentSession = Holder.talks.get(talker);
        box = (MessageInputToolBox)findViewById(R.id.message_box);
        box.setOnOperationListener(new OnOperationListener() {
            @Override
            public void send(String content) {
                Message message = new Message(0, 1, User.nickname, "avatar", currentSession.nickname, "avatar", content, true, true, new Date());
                adapter.getData().add(message);
                list.setSelection(list.getBottom());
                sendToPeer(content);
            }

            @Override
            public void selectedFace(String content) {

            }

            @Override
            public void selectedFuncation(int index) {

            }
        });


        initList();
    }

    private void initList(){
        for(int i = 0; i < 10; i++){
            faceNameList.add("big"+ i);
        }
        faceData.put(R.drawable.em_cate_magic, faceNameList);
        box.setFaceData(faceData);
        for(int i = 0;  i < 5; i++){
            Option takePhoto = new Option(this, "Take", R.drawable.take_photo);
            Option gallery = new Option(this, "gallery", R.drawable.gallery);
            functionData.add(gallery);
            functionData.add(takePhoto);
        }
        box.setFunctionData(functionData);
        list = (ListView) findViewById(R.id.message_list);
        ArrayList<Record> existsRecord = currentSession.getRecords();
        for(Record item : existsRecord){
            Message message = new Message(0, 1, Holder.peers.get(item.sender), "avatar", Holder.peers.get(item.receiver), "avatar", item.content, true, true, item.edit);
            messages.add(message);
        }
        adapter = new MessageAdapter(this, messages);
        list.setAdapter(adapter);
        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hide();
                return false;
            }
        });

    }

    private void sendToPeer(String content){
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.sendChat);
        intent.putExtra("content", content);
        startService(intent);
    }

}
