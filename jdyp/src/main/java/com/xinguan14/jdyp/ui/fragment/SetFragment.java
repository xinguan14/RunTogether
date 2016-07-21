package com.xinguan14.jdyp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinguan14.jdyp.R;
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
public class SetFragment extends ParentWithNaviFragment {

//    @Bind(R.id.tv_set_name)
//    TextView tv_set_name;
//
//    @Bind(R.id.layout_info)
//    RelativeLayout layout_info;

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
