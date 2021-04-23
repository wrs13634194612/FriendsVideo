package com.example.mepositry.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.example.mepositry.R;



//根据宽高比例自动计算高度ImageView

public class RatioImageView extends AppCompatImageView {

    private int playBtnRes = R.mipmap.play_btn_video;
    private Bitmap playBtnBitmap;

    private boolean type; //true表示video
    private int i; //i图片id
    private String url; //url图片地址


    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect src = new Rect();
    RectF dest = new RectF();
    //* 宽高比例
    private float mRatio = 0f;

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
     /*   TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        playBtnRes = typedArray.getResourceId(R.styleable.ImageViewPlay_ivp_play_btn_res, playBtnRes);
        playBtnBitmap = BitmapFactory.decodeResource(getResources(), playBtnRes);
        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f);
        typedArray.recycle();*/
    }

    public RatioImageView(Context context) {
        super(context);
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.RatioImageView);
        playBtnRes = typedArray.getResourceId(R.styleable.ImageViewPlay_ivp_play_btn_res, playBtnRes);
        playBtnBitmap = BitmapFactory.decodeResource(getResources(), playBtnRes);
        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f);
        typedArray.recycle();
    }



    //*description: 设置图片类型，如果是TYPE_IMAGE，显示图片，如果是TYPE_VIDEO，显示图片，并且在图片正中心绘制一个播放按钮
    public void setType(boolean type,  int i,   String url){
        this.type = type;
        this.i = i;
        this.url = url;
    }


    //设置ImageView的宽高比

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(type){
            //如果是true，显示图片，并且在图片正中心绘制一个播放按钮
            Drawable drawable = getDrawable();
            if (drawable != null) {
                int viewW = drawable.getIntrinsicWidth(); //获取图片的宽
                int viewH = drawable.getIntrinsicHeight(); //获取图片的高
                int btnW = playBtnBitmap.getWidth(); //获取播放按钮的宽
                int btnH = playBtnBitmap.getHeight(); //获取播放按钮的高
                float[] result = measureViewSize(viewW, viewH);
                if(result[0] > 0 && result[1] > 0){ //先根据比例缩放图标，确保绘制的时候再次回归缩放，保持播放的图片大小不变
                    btnW *= (viewW / result[0]);
                    btnH *= (viewH / result[1]);
                }
                float left = (viewW - btnW) / 2.0f;
                float top = (viewH - btnH) / 2.0f;
                src.set(0, 0, btnW, btnH);
                dest.set(left, top, left+btnW, top+btnH);
                canvas.save();
                canvas.concat(getImageMatrix());
                canvas.drawBitmap(playBtnBitmap, src, dest, mPaint);
                canvas.restore();
            }
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        Drawable drawable = getDrawable();
        if (drawable != null) { //重新计算view
            int viewW = drawable.getIntrinsicWidth();
            int viewH = drawable.getIntrinsicHeight();
            if(viewW > 0 && viewH > 0) {
                float[] result = measureViewSize(viewW, viewH);
                setMeasuredDimension((int)result[0], (int) result[1]);
            }
        }

        if (mRatio != 0) {
            float height = width / mRatio;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Drawable drawable = getDrawable();
                if (drawable != null) {
                    drawable.mutate().setColorFilter(Color.GRAY,
                            PorterDuff.Mode.MULTIPLY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Drawable drawableUp = getDrawable();
                if (drawableUp != null) {
                    drawableUp.mutate().clearColorFilter();
                }
                break;
        }

        return super.onTouchEvent(event);
    }


    // *description: 根据传入的图片宽高，计算出最终的imageview的宽高，长宽等比缩放
    private float[] measureViewSize(int w, int h) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        float maxW = lp.width;
        float maxH = lp.height;
        float showWidth = w;
        float showHeight = h;
        float scale = (1.0f * maxW) / maxH;
        float s = 1.0f * w / h;
        if (w < maxW && h < maxH) { //不进行缩放
            showWidth = w;
            showHeight = h;
        } else if (s > scale) { //宽取最大，高进行缩小
            showWidth = maxW;
            showHeight = (int) (h * (showWidth * 1.0 / w));
        } else if (s <= scale) {//高取最大，宽进行缩小
            showHeight = maxH;
            showWidth = (int) (w * (showHeight * 1.0 / h));
        }
        float[] result = new float[2];
        result[0] = showWidth;
        result[1] = showHeight;
        return result;
    }

}
