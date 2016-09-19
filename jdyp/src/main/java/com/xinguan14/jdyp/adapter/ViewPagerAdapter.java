package com.xinguan14.jdyp.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuling on 2016/9/10.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }
    public  ViewPagerAdapter(FragmentManager fragmentManager,List<Fragment> arrayList){
        super(fragmentManager);
        this.fragmentList=arrayList;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
    @Override
    public int getCount() {
        return fragmentList.size();
    }





}
