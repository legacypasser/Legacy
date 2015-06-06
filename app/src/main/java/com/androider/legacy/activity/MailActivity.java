package com.androider.legacy.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androider.legacy.R;
import com.androider.legacy.data.User;

public class MailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
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
        page.loadData("<html><head></head><body>" +
                "<a href=\"http://mail.163.com\">http://mail.163.com</a><br>" +
                "<a href=\"http://mail.126.com\">http://mail.126.com</a><br>" +
                "<a href=\"http://mail.qq.com\">http://mail.qq.com</a><br>" +
                "<a href=\"http://mail.sina.cn\">http://mail.sina.cn</a><br>" +
                "<a href=\"http://mail.foxmail.com\">http://mail.foxmail.com</a><br>" +
                "</body></html>", "text/html", null);
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
