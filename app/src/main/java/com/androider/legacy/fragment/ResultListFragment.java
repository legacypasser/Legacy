package com.androider.legacy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.listener.LeftClikedListener;
import com.androider.legacy.service.NetService;
import com.dexafree.materialList.cards.BigImageButtonsCard;
import com.dexafree.materialList.view.MaterialListView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultListFragment extends BaseListFragment {

    MaterialEditText searchInput;
    MaterialListView resultList;
    ImageButton searchButton;
    public static ResultListFragment instance;
    public static ResultListFragment newInstance(String param1, String param2) {
        ResultListFragment fragment = new ResultListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        searchInput = (MaterialEditText)rootView.findViewById(R.id.search_input);
        resultList = (MaterialListView)rootView.findViewById(R.id.result_list);
        searchButton = (ImageButton)rootView.findViewById(R.id.search_confirm);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        return rootView;
    }

    private void startSearch(){
        String keyword = searchInput.getText().toString();
        Intent intent = new Intent(getActivity(), NetService.class);
        intent.putExtra(Constants.intentType, Constants.searchReq);
        intent.putExtra(Constants.keyword, keyword);
        getActivity().startService(intent);
    }

    public void refreshList(){
        for(Post item : Holder.resultedPost){
            BigImageButtonsCard card = new BigImageButtonsCard(getActivity());
            Log.v("panob", item.abs);
            card.setDescription(item.abs);
            card.setLeftButtonText("see detail");
            card.setRightButtonText("right");
            card.setDrawable(R.drawable.ic_launcher);
            card.setOnLeftButtonPressedListener(new LeftClikedListener(item.id));
            resultList.add(card);
        }
    }
}
