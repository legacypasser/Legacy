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
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.getbase.floatingactionbutton.AddFloatingActionButton;


public class PostDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static int currentId;

    TextView des;
    TextView img;
    TextView nickname;
    AddFloatingActionButton startTalk;

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
        des = (TextView)rootView.findViewById(R.id.post_des);
        img = (TextView)rootView.findViewById(R.id.post_img);
        nickname = (TextView)rootView.findViewById(R.id.post_seller);
        startTalk = (AddFloatingActionButton)rootView.findViewById(R.id.chat_with_seller);
        startTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("seller", Post.get(currentId).seller);
                getActivity().startActivity(intent);
            }
        });
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
        des.setText(current.des);
        img.setText(current.img);
        nickname.setText(Holder.peers.get(current.seller));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
