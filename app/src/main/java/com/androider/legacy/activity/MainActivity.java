package com.androider.legacy.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.androider.legacy.R;
import com.androider.legacy.adapter.FragmentViewPagerAdapter;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.School;
import com.androider.legacy.data.Session;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Nicker;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.LoginFragment;
import com.androider.legacy.fragment.MyPostListFragment;
import com.androider.legacy.fragment.PostDetailFragment;
import com.androider.legacy.fragment.RecommendFragment;
import com.androider.legacy.fragment.SessionListFragment;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.LegacyTask;
import com.androider.legacy.net.NetConstants;
import com.androider.legacy.net.Receiver;
import com.androider.legacy.net.SearchClient;
import com.androider.legacy.net.Sender;
import com.androider.legacy.net.UdpClient;
import com.androider.legacy.service.ChatService;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.ConnectDetector;
import com.androider.legacy.util.SoundShouter;
import com.androider.legacy.util.StoreInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    public static String filePath;
    public static MainActivity instance;
    TextView accountEmail;
    TextView accountNickname;
    Thread sendThread;
    Thread receiveThread;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    SwitchCompat info;
    SwitchCompat shutter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        new DatabaseHelper(this);
        setContentView(R.layout.activity_main);
        initToolbar();
        Nicker.initNick(this);
        School.iniSchool(this);
        filePath = this.getApplicationContext().getFilesDir() + "/";
        User.instance.drag();
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
        drawer = (DrawerLayout)findViewById(R.id.legacy_drawer);
        Toolbar overallToolBar = (Toolbar)findViewById(R.id.overall_toolbar);
        setSupportActionBar(overallToolBar);
        toggle = new ActionBarDrawerToggle(this,drawer, overallToolBar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        accountEmail = (TextView)findViewById(R.id.account_email);
        accountNickname = (TextView)findViewById(R.id.account_nickname);
        shutter = (SwitchCompat)findViewById(R.id.shutter_switch);
        info = (SwitchCompat)findViewById(R.id.info_switch);
        if(StoreInfo.getBool(StoreInfo.shutter))
            shutter.setChecked(true);
        else
            shutter.setChecked(false);
        if(StoreInfo.getBool(StoreInfo.info))
            info.setChecked(true);
        else
            info.setChecked(false);

        shutter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    StoreInfo.setBool(StoreInfo.shutter, true);
                else
                    StoreInfo.setBool(StoreInfo.shutter, false);
            }
        });
        info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    StoreInfo.setBool(StoreInfo.info, true);
                else
                    StoreInfo.setBool(StoreInfo.info, false);
            }
        });
        ImageView feedback = (ImageView)findViewById(R.id.feedback);
        ImageView checkRefresh = (ImageView)findViewById(R.id.check_refresh);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.instance).setTitle("意见反馈").setMessage("请写邮件至welcome@hereprovides.com").setPositiveButton("确定",null).show();
            }
        });
        checkRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.instance, "此功能正在完善中", Toast.LENGTH_SHORT).show();
            }
        });
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
            Intent intent = new Intent(this, NetService.class);
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
        fragmentList.add(RecommendFragment.newInstance("", ""));
        fragmentList.add(MyPostListFragment.newInstance("",""));
        fragmentList.add(SessionListFragment.newInstance("", ""));
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        FragmentViewPagerAdapter pagerAdapter = new FragmentViewPagerAdapter( fragmentList,this.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        PageIndicator indicator = (TabPageIndicator)findViewById(R.id.pager_indicator);
        indicator.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
    }

    public void switchFragment(String fragmentName){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if(fragment == null){
            if(fragmentName.equals(LoginFragment.class.getSimpleName())){
                fragment = LoginFragment.newInstance("", "");
            }else if(fragmentName.equals(PostDetailFragment.class.getSimpleName())){
                fragment = PostDetailFragment.newInstance("", "");
            }
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment, fragmentName);
        ft.addToBackStack(null);
        ft.commit();
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
        if(id == R.id.action_register){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_login){
            switchFragment(LoginFragment.class.getSimpleName());
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
                Toast.makeText(instance, "邮箱不对哦，先去注册亲", Toast.LENGTH_SHORT).show();
                break;
            case Constants.password_fail:
                Toast.makeText(instance, "密码不对哦，亲", Toast.LENGTH_SHORT).show();
                break;
            case Constants.not_active:
                Toast.makeText(instance, "先去邮箱激活一下亲", Toast.LENGTH_SHORT).show();
                break;
            case Constants.unknow_login_fail:
                break;
            case Constants.userReseted:
                SessionListFragment.instance.clearSession();
            case Constants.userNotReseted:
                Toast.makeText(instance, "登陆成功" + User.instance.nickname, Toast.LENGTH_SHORT).show();
                loginSet();
                StoreInfo.setBool(StoreInfo.last, true);
                StoreInfo.setLong(StoreInfo.lastTime, System.currentTimeMillis());
                SessionListFragment.instance.startPull();
                instance.getSupportFragmentManager().popBackStack();
                break;
        }
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
                        Toast.makeText(instance, "这个邮箱已被注册过了，亲", Toast.LENGTH_SHORT).show();
                    else if(msg.arg1 == Constants.please_active){
                        Toast.makeText(instance, "注册成功，请登陆邮箱激活账号，么么哒", Toast.LENGTH_SHORT).show();
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
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        Sender.getInstance().sendToServer(""+User.instance.id, NetConstants.offline);
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
