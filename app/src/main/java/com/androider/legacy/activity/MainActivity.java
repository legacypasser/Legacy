package com.androider.legacy.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.androider.legacy.R;
import com.androider.legacy.adapter.FragmentViewPagerAdapter;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.School;
import com.androider.legacy.data.Session;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Nicker;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.LoginFragment;
import com.androider.legacy.fragment.MyPostListFragment;
import com.androider.legacy.fragment.PostDetailFragment;
import com.androider.legacy.fragment.RecommendFragment;
import com.androider.legacy.fragment.SessionListFragment;
import com.androider.legacy.net.NetConstants;
import com.androider.legacy.net.Receiver;
import com.androider.legacy.net.SearchClient;
import com.androider.legacy.net.Sender;
import com.androider.legacy.net.UdpClient;
import com.androider.legacy.service.ChatService;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.ConnectDetector;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.gc.materialdesign.views.ButtonFloat;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.viewpagerindicator.LinePageIndicator;

import net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    public static String filePath;
    private MaterialMenuIconToolbar materialMenu;
    public static MainActivity instance;
    TextView accountEmail;
    TextView accountNickname;
    Thread sendThread;
    Thread receiveThread;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        new DatabaseHelper(this);
        setContentView(R.layout.activity_main);
        drawer = (DrawerLayout)findViewById(R.id.legacy_drawer);
        Toolbar overallToolBar = (Toolbar)findViewById(R.id.overall_toolbar);
        setSupportActionBar(overallToolBar);
        overallToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StateController.getCurrent() == Constants.mainState) {
                    drawer.openDrawer(Gravity.START);
                    materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
                    StateController.change(Constants.drawerState);
                } else {
                    backControl();
                }

            }
        });
        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                if (StateController.getCurrent() == Constants.drawerState) {
                    materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
                    StateController.goBack();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (StateController.getCurrent() == Constants.mainState) {
                    drawer.openDrawer(Gravity.START);
                    materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
                    StateController.change(Constants.drawerState);
                }
            }
        });
        materialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.overall_toolbar;
            }
        };
        materialMenu.setNeverDrawTouch(true);
        Nicker.initNick(this);
        School.iniSchool(this);
        filePath = this.getApplicationContext().getFilesDir() + "/";
        StateController.change(Constants.mainState);
        User.instance.drag();
        autoLogin();
        initView();
    }

    private void backControl(){
        if(StateController.getCurrent() == Constants.detailState){
            getSupportFragmentManager().popBackStack();
            materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
            StateController.goBack();
        }else if (StateController.getCurrent() == Constants.mainState){
            finish();
        }else if(StateController.getCurrent() == Constants.drawerState){
            drawer.closeDrawer(Gravity.START);
            materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
            StateController.goBack();
        }
    }

    @Override
    public void onBackPressed() {
        backControl();
    }

    private void initLocation(){
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.baiduLocation);
        startService(intent);
    }

    private void autoLogin(){
        if(!ConnectDetector.isConnectedToNet()){
            return;
        }
        if(User.instance.id == -1){
            initLocation();
        }else{
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


        accountEmail = (TextView)findViewById(R.id.account_email);
        accountNickname = (TextView)findViewById(R.id.account_nickname);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(RecommendFragment.newInstance("", ""));
        fragmentList.add(MyPostListFragment.newInstance("",""));
        fragmentList.add(SessionListFragment.newInstance("", ""));
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        FragmentViewPagerAdapter pagerAdapter = new FragmentViewPagerAdapter( fragmentList,this.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        LinePageIndicator indicator = (LinePageIndicator)findViewById(R.id.pager_indicator);
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

        materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
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

        if(id == R.id.action_mail){
            Intent intent = new Intent(this, MailActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                case Constants.recommendAdded:
                    RecommendFragment.instance.refreshList();
                break;
                case Constants.detailRequest:
                    PostDetailFragment.instance.setView();
                    break;
                case Constants.registrationSent:
                    if(msg.arg1 == Constants.mail_occupied)
                        Toast.makeText(instance, "这个邮箱已被注册过了，亲", Toast.LENGTH_SHORT).show();
                    else if(msg.arg1 == Constants.please_active)
                        Toast.makeText(instance, "注册成功，请登陆邮箱激活账号，么么哒", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.loginAttempt:
                    switch (msg.arg1){
                        case Constants.email_fail:
                            break;
                        case Constants.password_fail:
                            break;
                        case Constants.not_active:
                            break;
                        case Constants.unknow_login_fail:
                            break;
                        case Constants.userReseted:
                            Session.clearSession();
                            SessionListFragment.instance.useNewAdapter();
                        case Constants.userNotReseted:
                            RecommendFragment.instance.request();
                            Toast.makeText(instance, "登陆成功 " + User.instance.nickname, Toast.LENGTH_SHORT).show();
                            instance.accountNickname.setText(User.instance.nickname);
                            instance.accountEmail.setText(User.instance.email);
                            UdpClient.getInstance().isRunning = true;
                            instance.chatOn();
                            SessionListFragment.instance.startPull();
                            if(StateController.getCurrent() == Constants.detailState){
                                instance.backControl();
                            }
                            User.instance.alreadLogin = true;
                            break;
                    }
                    break;
                case Constants.pullMsg:
                    SessionListFragment.instance.refreshSessions();
                    break;
                case Constants.myPublish:
                    MyPostListFragment.instance.addItem();
                    break;
                case Constants.sessionAdded:
                    SessionListFragment.instance.addSession(Holder.talks.get(msg.arg1));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper.db.close();
        chatOff();
    }

    public void chatOn(){
        UdpClient.getInstance().isRunning = true;
        UdpClient.getInstance().open();
            instance.receiveThread = new Thread(Receiver.getInstance());
            instance.receiveThread.start();
            instance.sendThread = new Thread(Sender.getInstance());
            instance.sendThread.start();
    }

    public void chatOff(){
        if(!UdpClient.getInstance().isRunning)
            return;
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.byebye);
        startService(intent);
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
