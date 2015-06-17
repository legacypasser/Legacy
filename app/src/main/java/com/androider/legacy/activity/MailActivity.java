package com.androider.legacy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v7.widget.Toolbar;

import com.androider.legacy.R;
import com.androider.legacy.data.User;
import com.androider.legacy.util.StoreInfo;

public class MailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.simple_toolbar);
        setSupportActionBar(toolbar);
        WebView page = (WebView)findViewById(R.id.webView);
        page.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
        page.setWebChromeClient(new WebChromeClient());
        page.getSettings().setJavaScriptEnabled(true);
        String email = StoreInfo.getString("email");
        String emailLink = null;
        if(email.contains("163"))
            emailLink = "http://m.mail.163.com";
        if(email.contains("126"))
            emailLink = "http://m.mail.126.com";
        if(email.contains("qq") || email.contains("foxmail"))
            emailLink = "http://m.mail.qq.com";
        if(email.contains("sina"))
            emailLink = "http://mail.sina.com.cn";
        page.loadUrl(emailLink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
