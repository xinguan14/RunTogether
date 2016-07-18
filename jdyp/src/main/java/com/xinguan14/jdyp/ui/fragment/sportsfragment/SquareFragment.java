package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.ListViewAdapter;
import com.xinguan14.jdyp.bean.GridViewItem;

import java.util.ArrayList;
import java.util.List;

public class SquareFragment extends android.support.v4.app.ListFragment {

	private View rootView;
	private List<GridViewItem> listgrid;
	private ListViewAdapter listViewAdapter;
	private ListView listView;
	private String imgs1;
	private  String imgs3;
	private  String imgs2;
	private String imgs4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_sport_main, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listgrid = new ArrayList<GridViewItem>();
		init();
		initData();
	}
	private void  init(){
		//listView = (ListView)findViewById(R.id.list);

	}

	private void initData(){
		imgs1="http://pic60.nipic.com/file/20150211/18733170_145247158001_2.jpg#"
				+"http://mvimg1.meitudata.com/566507ca1bcc65451.jpg";
		imgs2="http://rj1.douguo.net/upload/diet/6/6/8/666f180617cab130bef1dea9fb3f7fe8.jpg#" +
				"http://img4.duitang.com/uploads/blog/201312/01/20131201120117_F5QXY.jpeg#"+
				"http://www.sh.xinhuanet.com/133071048_13905438457501n.jpg";
		imgs3="http://t2.fansimg.com/uploads2011/02/userid290276time20110205120020.jpg#" +
				"http://image81.360doc.com/DownloadImg/2015/01/2113/49316679_9.jpg#" +
				"http://image.tianjimedia.com/uploadImages/2014/133/11/EN2I6768CHU1_1000x500.jpg#"+
				"http://pic72.nipic.com/file/20150716/6659253_104414205000_2.jpg#"+
				"http://pic36.nipic.com/20131222/10558908_214221305000_2.jpg";
		imgs4 = "http://h.hiphotos.baidu.com/zhidao/pic/item/5243fbf2b21193133f9f1e3967380cd790238d5f.jpg";
		GridViewItem gridViewItem = null;
		for(int i = 0;i<=3;i++){
			gridViewItem = new GridViewItem();
			switch (i){
				case 0:
					gridViewItem.setUsername("小仙女");
					gridViewItem.setHeadphoto("http://cdn.duitang.com/uploads/item/201412/12/20141212184514_BJjWy.jpeg");
					gridViewItem.setContent("啊啊啊啊啊啊啊萌死了");
					gridViewItem.setTime("1分钟前");
					gridViewItem.setImage(imgs1);
					break;
				case 1:
					gridViewItem.setUsername("喵呜是地球吗");
					gridViewItem.setHeadphoto("http://cdn.duitang.com/uploads/item/201501/19/20150119171935_ZkRsZ.thumb.224_0.jpeg");
					gridViewItem.setContent("好吃的日料 超级开心的啦啦啦");
					gridViewItem.setTime("3分钟前");
					gridViewItem.setImage(imgs2);
					break;
				case 2:
					gridViewItem.setUsername("ill_kaaa");
					gridViewItem.setHeadphoto("http://img5q.duitang.com/uploads/item/201404/03/20140403135406_XFS3M.jpeg");
					gridViewItem.setContent("呐呐呐呐呐");
					gridViewItem.setTime("5分钟前");
					gridViewItem.setImage(imgs3);
					break;
				case 3:
					gridViewItem.setUsername("Brark");
					gridViewItem.setHeadphoto("http://img3.imgtn.bdimg.com/it/u=3367770910,1075442079&fm=21&gp=0.jpg");
					gridViewItem.setContent("我又在写Bug了 难过");
					gridViewItem.setTime("5分钟前");
					gridViewItem.setImage(imgs4);
					break;
			}
			listgrid.add(gridViewItem);
		}
		listViewAdapter = new ListViewAdapter(getActivity(),listgrid);
		setListAdapter(listViewAdapter);
	}
}

