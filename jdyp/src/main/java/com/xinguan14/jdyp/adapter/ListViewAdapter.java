package com.xinguan14.jdyp.adapter;

import android.app.Activity;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinguan14.jdyp.MyGridView;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.bean.GridViewItem;
import com.xinguan14.jdyp.util.SysUtils;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyz on 2016/7/18.
 */
public class ListViewAdapter  extends BaseAdapter {
    private LayoutInflater mInflater;
    private Activity context;
    private List<GridViewItem> list;
    private FinalBitmap finalBitmap;
    private GridViewAdapter gridViewAdapter;
    private int wh;
    public ListViewAdapter(Activity context, List<GridViewItem> list){
        super();
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.wh=(SysUtils.getScreenWidth(context)- SysUtils.Dp2Px(context, 99))/3;
        this.list = list;
        this.finalBitmap = FinalBitmap.create(context);
        this.finalBitmap.configLoadfailImage(R.drawable.love);
    }
    public List<GridViewItem> getlist(){
        return  list;
    }
    @Override
    public int getCount() {

        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {

        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list == null ? null : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (list.size()==0){
            return null;
        }
        final ViewHolder holder;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.fragment_sport_square_item,null);
            holder = new ViewHolder();
            holder.headphoto = (ImageView) convertView.findViewById(R.id.info_iv_head);//头像
            holder.disName = (TextView) convertView.findViewById(R.id.info_tv_name);//昵称
            holder.time = (TextView) convertView.findViewById(R.id.info_tv_time);//时间
            holder.content = (TextView) convertView.findViewById(R.id.info_tv_content);//发布内容
            holder.rl4=(RelativeLayout) convertView.findViewById(R.id.rl4);//图片布局
            holder.gv_images = (MyGridView) convertView.findViewById(R.id.gv_images);//图片
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        final GridViewItem gridViewItem = list.get(position);
        String name = null,time = null,content = null,headpath = null,contentimage = null;
        if(gridViewItem !=null){
            name = gridViewItem.getUsername();
            time = gridViewItem.getTime();
            content = gridViewItem.getContent();
            headpath = gridViewItem.getHeadphoto();
            contentimage = gridViewItem.getImage();
        }
        //昵称
        if (name!=null&&!name.equals("")) {
            holder.disName.setText(name);
        }
        //是否含有图片
        if (contentimage!=null&&!contentimage.equals("")) {
            holder.rl4.setVisibility(View.VISIBLE);
            initInfoImages(holder.gv_images,contentimage);
        } else {
            holder.rl4.setVisibility(View.GONE);
        }
        //发布时间
        if (time!=null&&!time.equals("")) {
            holder.time.setText(time);
        }
        //内容
        if (content!=null&&!content.equals("")) {
            holder.content.setText(content);
            Linkify.addLinks(holder.content, Linkify.WEB_URLS);
        }
        //头像
        if (headpath!=null&&!headpath.equals("")) {
            finalBitmap.display(holder.headphoto,headpath);
        } else {
            holder.headphoto.setImageResource(R.drawable.love);
        }
        holder.headphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(context, "点击了头像", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }
    static class ViewHolder {
        ImageView headphoto;
        TextView disName;
        TextView time;
        TextView content;
        MyGridView gv_images;
        RelativeLayout rl4;
    }
    public void initInfoImages(MyGridView gv_images,final String imgspath){
        if(imgspath!=null&&!imgspath.equals("")){
            String[] imgs=imgspath.split("#");
            ArrayList<String> list=new ArrayList<String>();
            for(int i=0;i<imgs.length;i++){
                list.add(imgs[i]);
            }
            int w=0;
            switch (imgs.length) {
                case 1:
                    w=wh;
                    gv_images.setNumColumns(1);
                    break;
                case 2:
                case 4:
                    w=2*wh+SysUtils.Dp2Px(context, 2);
                    gv_images.setNumColumns(2);
                    break;
                case 3:
                case 5:
                case 6:
                    w=wh*3+ SysUtils.Dp2Px(context, 2)*2;
                    gv_images.setNumColumns(3);
                    break;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.WRAP_CONTENT);
            gv_images.setLayoutParams(lp);
            /*第一个参数为宽的设置，第二个参数为高的设置。
            如果将一个View添加到一个Layout中，最好告诉Layout用户期望的布局方式，也就是将一个认可的layoutParams传递进去
            但LayoutParams类也只是简单的描述了宽高，宽和高都可以设置成三种值：
            1，一个确定的值；
            2，FILL_PARENT，即填满（和父容器一样大小）；
            3，WRAP_CONTENT，即包裹住组件就好。。*/
            gridViewAdapter=new GridViewAdapter(context, list);
            gv_images.setAdapter(gridViewAdapter);
            gv_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Toast.makeText(context, "点击了第"+(arg2+1)+"张图片", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

}
