package com.androider.legacy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.User;
import com.androider.legacy.listener.BigImageListener;
import com.androider.legacy.listener.ImageListener;
import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;


public class PostDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static int currentId;

    MaterialListView detailHolder;
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
        detailHolder = (MaterialListView)rootView.findViewById(R.id.detail_holder);
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
            BigImageCard card = new BigImageCard(getActivity());
            card.setDescription("");
            card.setTitle("");
            card.setDrawable(R.drawable.ic_launcher);
            ImageLoader.getInstance().loadImage(Constants.imgPath + item, new BigImageListener(card));
            detailHolder.add(card);
        }

        SmallImageCard descripCard = new SmallImageCard(getActivity());
        descripCard.setTitle("");
        descripCard.setDescription(current.des);
        descripCard.setDrawable(R.drawable.ic_launcher);
        SmallImageCard ownerCard = new SmallImageCard(getActivity());
        ownerCard.setTitle("");
        ownerCard.setDescription(User.getPeerNick(current.seller));
        ownerCard.setDrawable(R.drawable.ic_launcher);

        detailHolder.add(descripCard);
        detailHolder.add(ownerCard);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
