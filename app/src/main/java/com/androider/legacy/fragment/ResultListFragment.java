package com.androider.legacy.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultListFragment extends BaseListFragment {

    public static ResultListFragment newInstance(String param1, String param2) {
        ResultListFragment fragment = new ResultListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

}
