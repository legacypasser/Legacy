package com.androider.legacy.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.Session;
import com.androider.legacy.data.User;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.net.LegacyTask;
import com.androider.legacy.util.DateConverter;
import com.androider.legacy.util.StoreInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailActivity extends AppCompatActivity {
    TextView detailDes;
    TextView detailNickname;
    CardView detailNickCard;
    TextView detailPub;
    TextView detailTitle;
    LinearLayout imgHolder;
    TextView detailPrice;
    TextView detailSchool;
    ImageView icon;
    private DetailActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar)findViewById(R.id.simple_toolbar));
        instance = this;
        imgHolder = (LinearLayout)findViewById(R.id.img_holder);
        detailDes = (TextView)findViewById(R.id.detail_des);
        detailPub = (TextView)findViewById(R.id.detail_pub);
        detailNickname = (TextView)findViewById(R.id.detail_nickname);
        detailNickCard = (CardView)findViewById(R.id.detail_nick_card);
        detailTitle = (TextView)findViewById(R.id.detail_title);
        detailSchool = (TextView)findViewById(R.id.detail_school);
        detailPrice = (TextView)findViewById(R.id.detail_price);
        icon = (ImageView)findViewById(R.id.msg_icon);
        setView();
    }

    public void setView(){
        final Post current = (Post)getIntent().getSerializableExtra(Constants.detail);
        if(current.des.equals(Constants.emptyString)){
            Post.get(current.id, new LegacyTask.RequestCallback() {
                @Override
                public void onRequestDone(String result) {
                    current.des = result;
                    ContentValues cv = new ContentValues();
                    cv.put(getString(R.string.raw_des), current.des);
                    DatabaseHelper.db.update(Post.tableName, cv, "id = ?", new String[]{Constants.emptyString + current.id});
                    fillContent(current);
                    fillSeller(current.seller);
                }
            });
        }else {
            fillContent(current);
            fillSeller(current.seller);
        }
    }

    private void fillSeller(final int id){
        if(id == User.instance.id) {
            icon.setImageResource(R.drawable.ic_account_circle_white_36dp);
            detailNickname.setText(getString(R.string.my_legacy));
            detailSchool.setText(User.instance.school);
            return;
        }
        final Mate one = Mate.getPeer(id);
        if (one == null){
            Mate.getPeer(id, new LegacyTask.RequestCallback() {
                @Override
                public void onRequestDone(String result) {
                    Mate neted = Mate.fromString(result);
                    setEntry(neted);
                }
            });
        }else {
            setEntry(one);
        }
    }

    private void setEntry(final Mate one){
        detailNickname.setText(one.nickname);
        detailSchool.setText(one.school);
        if(StoreInfo.validLogin()){
            detailNickCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(instance, ChatActivity.class);
                    intent.putExtra(Constants.chat, Session.get(one));
                    instance.startActivity(intent);
                }
            });
        }else {
            detailNickCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(instance, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void fillContent(Post current){
        for(String item : current.getDetailImg()){
            View imageCard = LayoutInflater.from(this).inflate(R.layout.item_img, null);
            ImageView img = (ImageView)imageCard.findViewById(R.id.detail_img);
            ImageLoader.getInstance().displayImage(item, img);
            imgHolder.addView(imageCard);
        }
        detailDes.setText(current.des);
        detailPrice.setText(getString(R.string.price_value) + current.price + getString(R.string.rmb));
        detailPub.setText(DateConverter.formatDate(current.publish));
        detailTitle.setText(current.abs);
    }

}
