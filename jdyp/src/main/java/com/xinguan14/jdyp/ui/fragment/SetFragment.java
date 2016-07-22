package com.xinguan14.jdyp.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.StikkyHeader.example.AchievementsFragment;
import com.xinguan14.jdyp.StikkyHeader.example.DynamicFragment;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.model.UserModel;
import com.xinguan14.jdyp.ui.LoginActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;

/**设置
 * @author :smile
 * @project:SetFragment
 * @date :2016-01-25-18:23
 */
//
public class SetFragment extends ParentWithNaviFragment{
    private  SetFragment setFragment;

//    @Bind(R.id.tv_set_name)
//    TextView tv_set_name;
//
//    @Bind(R.id.layout_info)
//    RelativeLayout layout_info;

    private
    FragmentManager manager;
    private
    FragmentTransaction ft;
    SetFragment fragment;
    @Override
    protected String title() {
        return "我的";
    }

    public static SetFragment newInstance() {
        SetFragment fragment = new SetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SetFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_set,container, false);
        initNaviView();


        ButterKnife.bind(this, rootView);
//        String username = UserModel.getInstance().getCurrentUser().getUsername();
//        tv_set_name.setText(TextUtils.isEmpty(username)?"":username);
        return rootView;
    }
    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = (ListView) getActivity().findViewById(R.id.listview);
        manager=getFragmentManager();
        String[] mFrags = {
                "最新成就",

                "我的动态",

        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFrags);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;

                LinearLayout bottom = (LinearLayout) getActivity().findViewById(R.id.bottom);
                LinearLayout gooey = (LinearLayout) getActivity().findViewById(R.id.gooey);
                FrameLayout content = (FrameLayout) getActivity().findViewById(R.id.id_content);
                bottom.setVisibility(View.GONE);
                gooey.setVisibility(View.GONE);

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) content.getLayoutParams();
                layoutParams.bottomMargin=0;//将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
                content.setLayoutParams(layoutParams);


                switch (position) {
                    case 0:
                        fragment = new AchievementsFragment();

                        break;

                    case 1:
                        fragment = new DynamicFragment();
                        break;

                }


                ft=manager.beginTransaction();
                ft.replace(R.id.id_content,fragment);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
    }


    /*
        // /点击进入个人信息
        @OnClick(R.id.layout_info)
        public void onInfoClick(View view){
            Bundle bundle = new Bundle();
            bundle.putSerializable("u", BmobUser.getCurrentUser(getActivity(),User.class));
            startActivity(UserInfoActivity.class,bundle);
        }
    */
    @OnClick(R.id.btn_logout)
    public void onLogoutClick(View view){
        UserModel.getInstance().logout();
        //可断开连接
        BmobIM.getInstance().disConnect();
        getActivity().finish();
        startActivity(LoginActivity.class,null);
    }

}
