package com.androider.legacy.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.Session;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.SessionListFragment;
import com.androider.legacy.net.NetConstants;
import com.androider.legacy.net.Sender;
import com.androider.legacy.service.ChatService;
import com.androider.legacy.service.NetService;
import com.jialin.chat.Message;
import com.jialin.chat.MessageAdapter;
import com.jialin.chat.MessageInputToolBox;
import com.jialin.chat.OnOperationListener;
import com.jialin.chat.Option;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ChatActivity extends AppCompatActivity {

    public int talker;
    public Session currentSession;
    public static ChatActivity instance;
    public static Record justAdded;
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
        currentSession = Session.get(talker);
        box = (MessageInputToolBox)findViewById(R.id.message_box);
        box.setOnOperationListener(new OnOperationListener() {
            @Override
            public void send(String content) {
                sendOnline(content);
            }

            @Override
            public void selectedFace(String content) {

            }

            @Override
            public void selectedFuncation(int index) {

            }
        });
        getSupportActionBar().setTitle(Mate.peers.get(currentSession.peer).nickname);
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
            messages.add(item.formMessage());
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
        list.setSelection(list.getBottom());
    }


    private void sendOnline(String content){
        if(User.instance.alreadLogin){
            Intent intent = new Intent(this, ChatService.class);
            intent.putExtra(Constants.intentType, NetConstants.sendChat);
            intent.putExtra(NetConstants.content, content);
            startService(intent);
        }else
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
    }

    private void updateSession(){
        adapter.getData().add(justAdded.formMessage());
        list.setSelection(list.getBottom());
    }

    public NetHandler netHandler = new NetHandler(instance);

    private static class NetHandler extends Handler {
        WeakReference<ChatActivity> activityWeakReference;
        NetHandler(ChatActivity chatActivity){
            activityWeakReference = new WeakReference<>(chatActivity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case NetConstants.offline:
                    break;
                case NetConstants.feedback:
                    break;
                case NetConstants.message:
                    break;
            }
            ChatActivity.instance.updateSession();
        }
    }


}
