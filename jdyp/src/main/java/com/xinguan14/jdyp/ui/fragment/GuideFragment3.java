package com.xinguan14.jdyp.ui.fragment;

;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.ui.LoginActivity;

/**
 * Created by xuling on 2016/9/10.
 */
public class GuideFragment3 extends Fragment {
    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guide3, container, false);
        view.findViewById(R.id.iv_start).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goHome();
                    }
                }
        );
        return view;
    }

    private void goHome() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void setGuided() {
        SharedPreferences preferences = getActivity().getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();

    }
}
