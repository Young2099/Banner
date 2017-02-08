package com.yf.banner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by ${yf} on 2017/2/8.
 * 设置在viewpager显示的圆点指示器
 */

public class IndicatorView extends LinearLayout implements HintView {
    //圆点的集合
    private ImageView[] mDots;
    //圆点的个数
    private int length = 0;
    private int lastPosition = 0;


    private Drawable dot_normal;
    private Drawable dot_focus;

    public IndicatorView(Context context) {
        super(context);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 用方法设置圆点指示器的大小和颜色
     *
     * @param size
     * @param focusColor
     * @param normalColor
     */
    public void setColorAndSize(int size, int focusColor, int normalColor) {
        removeAllViews();
        setOrientation(HORIZONTAL);
        this.length = size;
        mDots = new ImageView[length];
        dot_focus = makeFocusDrawable(focusColor);
        dot_normal = makeNormalDrawable(normalColor);
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new ImageView(getContext());
            LayoutParams dotlp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            dotlp.setMargins(10, 0, 10, 10);
            mDots[i].setLayoutParams(dotlp);
            mDots[i].setBackground(dot_normal);
            addView(mDots[i]);
        }
        setCurrent(0);
    }

    private Drawable makeNormalDrawable(int normalColor) {
        GradientDrawable dotNormal = new GradientDrawable();
        dotNormal.setColor(normalColor);
        dotNormal.setCornerRadius(Util.dip2px(getContext(), 4));
        dotNormal.setSize(Util.dip2px(getContext(), 8), Util.dip2px(getContext(), 8));
        return dotNormal;
    }

    private Drawable makeFocusDrawable(int focusColor) {
        GradientDrawable dotFoucs = new GradientDrawable();
        dotFoucs.setColor(focusColor);
        dotFoucs.setCornerRadius(Util.dip2px(getContext(), 4));
        dotFoucs.setSize(Util.dip2px(getContext(), 8), Util.dip2px(getContext(), 8));
        return dotFoucs;
    }

    @Override
    public void setCurrent(int current) {
        if (current < 0 || current > length - 1) {
            return;
        }
        mDots[lastPosition].setBackground(dot_normal);
        mDots[current].setBackground(dot_focus);
        //将显示后position赋值给lastPosition;
        lastPosition = current;
    }
}
