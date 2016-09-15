package com.xinguan14.jdyp.ui.fragment.setfragment;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.stikkyHeader.Utils;
import com.xinguan14.jdyp.stikkyHeader.core.AnimatorBuilder;
import com.xinguan14.jdyp.stikkyHeader.core.BaseStickyHeaderAnimator;
import com.xinguan14.jdyp.stikkyHeader.core.HeaderStikkyAnimator;
import com.xinguan14.jdyp.stikkyHeader.core.StikkyHeaderBuilder;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.fragment.SetFragment;


/**
 * 最近成就
 *
 * @author 徐玲
 */
public class AchievementsFragment extends ParentWithNaviFragment {

    private View mToolbar;
    private ListView mListView;

    public AchievementsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.listview);
        mToolbar = view.findViewById(R.id.toolbar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseStickyHeaderAnimator animator = new HeaderStikkyAnimator() {

            @Override
            public AnimatorBuilder getAnimatorBuilder() {

                View viewToAnimate = getHeader().findViewById(R.id.header_image);
                final View titleToolbar = mToolbar.findViewById(R.id.title_toolbar);
                final Rect squareSizeToolbar = new Rect(0, 0, mToolbar.getHeight(), mToolbar.getHeight());

                return AnimatorBuilder.create()
                        .applyScale(viewToAnimate, squareSizeToolbar)
                        .applyTranslation(viewToAnimate, new Point(titleToolbar.getRight(), 0))
                        .applyFade(viewToAnimate, 1f);
            }

        };

        StikkyHeaderBuilder.stickTo(mListView)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeader(250)
                .animator(animator)
                .build();

        Utils.populateListView(mListView);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity()instanceof SetFragment.AddMenu){
            ((SetFragment.AddMenu) getActivity()).showMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof SetFragment.HideTab) {
            ((SetFragment.HideTab) getActivity()).hide();
        }
    }

    @Override
    protected String title() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
