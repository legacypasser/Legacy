package com.androider.legacy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.data.User;

import com.androider.legacy.util.DensityUtil;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.zip.Inflater;


public class PostDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static int currentId;
    TextView detailDes;
    TextView detailNickname;
    CardView detailNickCard;
    TextView detailPub;
    LinearLayout detailHolder;
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_detail, container, false);
        detailHolder = (LinearLayout)rootView.findViewById(R.id.detail_holder);
        detailDes = (TextView)rootView.findViewById(R.id.detail_des);
        detailPub = (TextView)rootView.findViewById(R.id.detail_pub);
        detailNickname = (TextView)rootView.findViewById(R.id.detail_nickname);
        detailNickCard = (CardView)rootView.findViewById(R.id.detail_nick_card);
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
        String[] imgs = current.img.split(";");
        for(String item : imgs){
            View img = LayoutInflater.from(getActivity()).inflate(R.layout.item_img, detailHolder, false);
            SelectableRoundedImageView imgItem = (SelectableRoundedImageView)img.findViewById(R.id.detail_img);
            ImageLoader.getInstance().displayImage(Constants.imgPath + item, imgItem);
            detailHolder.addView(img);
        }

        detailNickname.setText(Holder.peers.get(current.seller));
        detailDes.setText(current.des);
        detailPub.setText(PostConverter.formater.format(current.publish));
        detailNickCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("talker", Post.get(currentId).seller);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
