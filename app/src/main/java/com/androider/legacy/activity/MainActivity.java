package com.androider.legacy.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import com.androider.legacy.R;
import com.androider.legacy.adapter.FragmentViewPagerAdapter;
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.School;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Nicker;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.MyPostListFragment;
import com.androider.legacy.fragment.RecommendFragment;
import com.androider.legacy.fragment.SessionListFragment;
import com.androider.legacy.net.NetConstants;
import com.androider.legacy.net.Receiver;
import com.androider.legacy.net.Sender;
import com.androider.legacy.net.UdpClient;
import com.androider.legacy.service.AccountService;
import com.androider.legacy.util.ConnectDetector;
import com.androider.legacy.util.SoundShouter;
import com.androider.legacy.util.StoreInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    public static String filePath;
    public static MainActivity instance;
    private FloatingActionButton overButton;
    TextView accountEmail;
    TextView accountNickname;
    Thread sendThread;
    Thread receiveThread;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    private static final String inited = "inited";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        new DatabaseHelper(this);
        User.instance.drag();
        if(!StoreInfo.getBool(inited)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Nicker.initNick(instance);
                    School.iniSchool(instance);
                    Message msg = Message.obtain();
                    msg.what = Constants.initFinish;
                    try {
                        new Messenger(MainActivity.instance.netHandler).send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            StoreInfo.setBool(inited, true);
        }else{
            Nicker.initNick(instance);
            School.iniSchool(instance);
        }
        setContentView(R.layout.activity_main);
        initToolbar();
        filePath = this.getApplicationContext().getFilesDir() + "/";
        initView();
        autoLogin();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void initToolbar(){
        overButton = (FloatingActionButton)findViewById(R.id.over_float);
        overButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.instance, PublishActivity.class);
                MainActivity.instance.startActivity(intent);
            }
        });
        drawer = (DrawerLayout)findViewById(R.id.legacy_drawer);
        Toolbar overallToolBar = (Toolbar)findViewById(R.id.overall_toolbar);
        setSupportActionBar(overallToolBar);
        toggle = new ActionBarDrawerToggle(this,drawer, overallToolBar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        accountEmail = (TextView)findViewById(R.id.account_email);
        accountNickname = (TextView)findViewById(R.id.account_nickname);
    }

    private void loginSet(){
        instance.accountNickname.setText(User.instance.nickname);
        instance.accountEmail.setText(User.instance.email);
        Mate.peers.put(User.instance.id, User.instance);
        if(ConnectDetector.isConnectedToNet())
            chatOn();
    }

    private void autoLogin(){
        if(StoreInfo.validLogin()){
            loginSet();
        }else if(User.instance.id != -1 && ConnectDetector.isConnectedToNet()){
            Intent intent = new Intent(this, AccountService.class);
            intent.putExtra(Constants.intentType, Constants.loginAttempt);
            startService(intent);
        }
    }

    private void initView(){
        DisplayImageOptions options  = new DisplayImageOptions.Builder().
                cacheOnDisk(true).
                displayer(new SimpleBitmapDisplayer()).
                build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(RecommendFragment.newInstance(Constants.emptyString, Constants.emptyString));
        fragmentList.add(MyPostListFragment.newInstance(Constants.emptyString,Constants.emptyString));
        fragmentList.add(SessionListFragment.newInstance(Constants.emptyString, Constants.emptyString));
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        FragmentViewPagerAdapter pagerAdapter = new FragmentViewPagerAdapter( fragmentList,this.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        TabLayout tab = (TabLayout)findViewById(R.id.main_tab);
        tab.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(!ConnectDetector.isConnectedToNet()){
            Toast.makeText(this, getString(R.string.no_net), Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.lets_search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void afterLogin(int result){
        switch (result){
            case Constants.email_fail:
                Toast.makeText(instance, getString(R.string.email_wrong), Toast.LENGTH_SHORT).show();
                break;
            case Constants.password_fail:
                Toast.makeText(instance, getString(R.string.password_wrong), Toast.LENGTH_SHORT).show();
                break;
            case Constants.not_active:
                Toast.makeText(instance, getString(R.string.not_active), Toast.LENGTH_SHORT).show();
                break;
            case Constants.unknow_login_fail:
                break;
            case Constants.userReseted:
                SessionListFragment.instance.clearSession();
            case Constants.userNotReseted:
                Toast.makeText(instance, instance.getString(R.string.login_success) + User.instance.nickname, Toast.LENGTH_SHORT).show();
                loginSet();
                StoreInfo.setBool(StoreInfo.last, true);
                StoreInfo.setLong(StoreInfo.lastTime, System.currentTimeMillis());
                SessionListFragment.instance.startPull();
                instance.getSupportFragmentManager().popBackStack();
                break;
        }
    }

    public void showWelcome(){
        Toast.makeText(this, getString(R.string.welcome_register), Toast.LENGTH_LONG).show();
    }

    public NetHandler netHandler = new NetHandler(instance);

    private static class NetHandler extends Handler{
        WeakReference<MainActivity> activityWeakReference;
        NetHandler(MainActivity mainActivity){
            activityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.registrationSent:
                    if(msg.arg1 == Constants.mail_occupied)
                        Toast.makeText(instance, instance.getString(R.string.email_registered), Toast.LENGTH_SHORT).show();
                    else if(msg.arg1 == Constants.please_active){
                        Toast.makeText(instance, instance.getString(R.string.register_finish), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(instance, MailActivity.class);
                        instance.startActivity(intent);
                    }
                    break;
                case Constants.loginAttempt:
                    instance.afterLogin(msg.arg1);
                    break;
                case Constants.myPublish:
                    MyPostListFragment.instance.addItem();
                    break;
                case Constants.msgArrive:
                    if(msg.arg1 == NetConstants.message)
                        SoundShouter.playInfo();
                    SessionListFragment.instance.oneCome((Record)msg.getData().getSerializable(Constants.chat));
                    break;
                case Constants.initFinish:
                    instance.showWelcome();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if(UdpClient.getInstance().isRunning)
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        Sender.getInstance().sendToServer(Constants.emptyString+User.instance.id, NetConstants.offline);
                        UdpClient.getInstance().close();
                    }
                }).start();
                finish();

        }
    }

    @Override
    protected void onDestroy() {
        DatabaseHelper.db.close();
        chatOff();
        super.onDestroy();
    }

    public void chatOn(){
        UdpClient.getInstance().isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                InetAddress address = null;
                try {
                    address = InetAddress.getByName(NetConstants.serverAddr);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                UdpClient.getInstance().targetServer = new InetSocketAddress(address, NetConstants.serverPort);
                UdpClient.getInstance().open();
                instance.receiveThread = new Thread(Receiver.getInstance());
                instance.receiveThread.start();
                instance.sendThread = new Thread(Sender.getInstance());
                instance.sendThread.start();
            }
        }).start();
    }

    public void chatOff(){
        if(!UdpClient.getInstance().isRunning)
            return;
        UdpClient.getInstance().isRunning = false;
        if(receiveThread != null){
            receiveThread.interrupt();
            receiveThread = null;
        }
        if(sendThread != null){
            sendThread.interrupt();
            sendThread = null;
        }
    }
}
