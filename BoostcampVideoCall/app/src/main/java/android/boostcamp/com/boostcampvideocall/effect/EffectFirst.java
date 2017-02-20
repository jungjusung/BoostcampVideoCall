package android.boostcamp.com.boostcampvideocall.effect;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Jusung on 2017. 2. 19..
 */

public class EffectFirst extends ImageView {
    private int winX;
    private int winY;
    private int x;
    private int y;
    private int width;
    private int heigth;

    ViewGroup.LayoutParams params;

    public EffectFirst(Context context, int winX, int winY, int width, int height) {
        super(context);
        this.x = winX - width / 2;
        this.y = winY - height / 2;
        this.width = width;
        this.heigth = height;

        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
    }

}
