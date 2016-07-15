package com.xinguan14.jdyp.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinguan14.jdyp.R;

public class RunFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_run, container, false);

		initView(rootView);

		return rootView;
	}

	private void initView(View view) {

	}
}
