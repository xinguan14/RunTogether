package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinguan14.jdyp.R;

public class NearFragment extends Fragment {

	private View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_sport_near, container, false);
		return rootView;
	}
}
