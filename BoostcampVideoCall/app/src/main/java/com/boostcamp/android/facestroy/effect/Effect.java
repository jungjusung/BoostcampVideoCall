package com.boostcamp.android.facestroy.effect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Jusung on 2017. 2. 19..
 */

public class Effect extends ImageView {

    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;
    private ViewGroup.LayoutParams mParams;

    public Effect(Context context, int winX, int winY, int width, int height) {
        super(context);
        this.mX = winX - width / 2;
        this.mY = winY - height / 2;
        this.mWidth = width;
        this.mHeight = height;
        this.setVisibility(View.GONE);
        mParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(mParams);
    }

}
