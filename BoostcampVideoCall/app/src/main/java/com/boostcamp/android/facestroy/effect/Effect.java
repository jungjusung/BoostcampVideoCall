package com.boostcamp.android.facestroy.effect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Jusung on 2017. 2. 19..
 */

public class Effect extends ImageView {
    private int winX;
    private int winY;
    private int x;
    private int y;
    private int width;
    private int heigth;

    ViewGroup.LayoutParams params;

    public Effect(Context context, int winX, int winY, int width, int height) {
        super(context);
        this.x = winX - width / 2;
        this.y = winY - height / 2;
        this.width = width;
        this.heigth = height;
        this.setVisibility(View.GONE);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(params);
    }

}
