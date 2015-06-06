package com.androider.legacy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.adapter.ImageAdapter;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.data.User;

import com.androider.legacy.util.DateConverter;
import com.androider.legacy.util.DensityUtil;
import com.androider.legacy.util.FitManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.zip.Inflater;


public class PostDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    public static int currentId;
    TextView detailDes;
    TextView detailNickname;
    CardView detailNickCard;
    TextView detailPub;
    TextView detailTitle;
    LinearLayout imgHolder;
    TextView detailPrice;
    TextView detailSchool;
    ImageView icon;
    public static PostDetailFragment instance;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDetailFragment newInstance(String param1, String param2) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PostDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_detail, container, false);
        imgHolder = (LinearLayout)rootView.findViewById(R.id.img_holder);
        detailDes = (TextView)rootView.findViewById(R.id.detail_des);
        detailPub = (TextView)rootView.findViewById(R.id.detail_pub);
        detailNickname = (TextView)rootView.findViewById(R.id.detail_nickname);
        detailNickCard = (CardView)rootView.findViewById(R.id.detail_nick_card);
        detailTitle = (TextView)rootView.findViewById(R.id.detail_title);
        detailSchool = (TextView)rootView.findViewById(R.id.detail_school);
        detailPrice = (TextView)rootView.findViewById(R.id.detail_price);
        icon = (ImageView)rootView.findViewById(R.id.msg_icon);
        StateController.change(Constants.detailState);
        setView();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void setView(){
        Post current = Post.get(currentId);
        if(current == null)
            return;
        for(String item : current.getDetailImg()){
            View imageCard = LayoutInflater.from(getActivity()).inflate(R.layout.item_img, null);
            ImageView img = (ImageView)imageCard.findViewById(R.id.detail_img);
            ImageLoader.getInstance().displayImage(item, img);
            imgHolder.addView(imageCard);
        }
        detailDes.setText(current.des);
        detailPrice.setText("价格：" + current.price + "元");
        detailPub.setText(DateConverter.formatDate(current.publish));
        detailTitle.setText(current.abs);
        if(current.seller != User.instance.id){
            detailNickCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("talker", Post.get(currentId).seller);
                    getActivity().startActivity(intent);
                }
            });
            detailNickname.setText(Mate.peers.get(current.seller).nickname);
            detailSchool.setText(Mate.getPeer(current.seller).school);
        }else {
            icon.setImageResource(R.drawable.ic_person_black_48dp);
            detailNickname.setText("我的宝贝");
            detailSchool.setText(User.instance.school);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
