package com.xinguan14.jdyp.MyVeiw;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by wm on 2016/7/18.
 */
public class MyGridView extends GridView {


    public MyGridView(Context context) {

        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
   /* AttributeSet 是接收xml中定义的属性信息
    super后加参数的是用来调用父类中具有相同形式的构造函数
    “this通常指代当前对象，super通常指代父类*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try{
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            getLayoutParams().height = getMeasuredHeight();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
   /*
    MeasureSpec.AT_MOST这个是由我们给出的尺寸大小和模式生成一个包含这两个信息的int变量，这里这个模式这个参数，传三个常量中的一个。
    public static int makeMeasureSpec(int size, int mode)
    这个也就是父组件，能够给出的最大的空间，当前组件的长或宽最大只能为这么大，当然也可以比这个小。
    onMeasure方法是测量view和它的内容，决定measured width和measured height的，这个方法由 measure(int, int)方法唤起，子类可以覆写onMeasure来提供更加准确和有效的测量。
    其中两个输入参数：
            　　widthMeasureSpec
    　　heightMeasureSpec
    　　分别是parent提出的水平和垂直的空间要求。
            　　这两个要求是按照View.MeasureSpec类来进行编码的。
            　　参见View.MeasureSpec这个类的说明：这个类包装了从parent传递下来的布局要求，传递给这个child。
            　　每一个MeasureSpec代表了对宽度或者高度的一个要求。
            　　每一个MeasureSpec有一个尺寸（size）和一个模式（mode）构成。
            　　MeasureSpecs这个类提供了把一个的元组包装进一个int型的方法，从而减少对象分配。当然也提供了逆向的解析方法，从int值中解出size和mode*/
}
