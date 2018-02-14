package io.weichao.autosizetextview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.weichao.autosizetextview.R;

/**
 * Created by WEI CHAO on 2018/2/14.
 */
public class AutoSizeTextView extends View {
    private static final String TAG = "AutoSizeTextView";

    public static final int GRAVITY_LEFT = 0;
    public static final int GRAVITY_CENTER = 1;

    public static final int TEXT_STYLE_NORMAL = 0;
    public static final int TEXT_STYLE_BOLD = 1;
    public static final int TEXT_STYLE_ITALIC = 2;

    private int mGravity = GRAVITY_LEFT;
    private String mText;
    private int mTextColor = Color.DKGRAY;
    private float mGradient = 1;
//    private float mLetterSpacing = 0;
    private int mTextStyle = TEXT_STYLE_NORMAL;

    private TextPaint paint;
    private Paint.FontMetrics fontMetrics;

    public AutoSizeTextView(Context context) {
        this(context, null);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.AutoSizeTextView);
        if (attributes != null) {
            mGravity = attributes.getInt(R.styleable.AutoSizeTextView_gravity, mGravity);
            mText = attributes.getString(R.styleable.AutoSizeTextView_text);
            mTextColor = attributes.getColor(R.styleable.AutoSizeTextView_textColor, mTextColor);
            mGradient = attributes.getFloat(R.styleable.AutoSizeTextView_gradient, mGradient);
            mTextStyle = attributes.getInt(R.styleable.AutoSizeTextView_textStyle, mTextStyle);
//            mLetterSpacing = attributes.getFloat(R.styleable.AutoSizeTextView_letterSpacing, mLetterSpacing);
            attributes.recycle();
        }

        init();
    }

    private void init() {
        paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mTextColor);
        switch (mTextStyle) {
            case TEXT_STYLE_NORMAL:
                paint.setTypeface(Typeface.DEFAULT);
                break;
            case TEXT_STYLE_BOLD:
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                break;
            case TEXT_STYLE_ITALIC:
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "widthSize: " + widthSize);
        Log.d(TAG, "heightSize: " + heightSize);

        paint.setTextSize(heightSize);
        float textWidth;
        if (TextUtils.isEmpty(mText)) {
            textWidth = widthSize;
        } else {
            textWidth = paint.measureText(mText);
            if (textWidth > widthSize) {
                textWidth = getMaxTextSizeForWidth(widthSize, textWidth);
            }
        }
        Log.d(TAG, "textWidth: " + textWidth);

        setMeasuredDimension(widthSize, heightSize);
    }

    private float getMaxTextSizeForWidth(float widthSize, float textWidth) {
        if (textWidth <= widthSize) {
            return textWidth;
        } else {
            float textSize = paint.getTextSize();
            paint.setTextSize(textSize - mGradient);
            textWidth = paint.measureText(mText);
            Log.d(TAG, "getMaxTextSizeForWidth() textWidth: " + textWidth);
            return getMaxTextSizeForWidth(widthSize, textWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float marginLeft = 0, marginBottom = 0;
        if (mGravity == GRAVITY_CENTER) {
            float textWidth = paint.measureText(mText);
            marginLeft = (getMeasuredWidth() - textWidth) * 0.5f;
        }
        float totalX = marginLeft;
        float totalY = getMeasuredHeight() - marginBottom;
        if (fontMetrics == null) {
            fontMetrics = paint.getFontMetrics();
        }
        if (TextUtils.isEmpty(mText)) {
            canvas.drawText(" ", totalX, totalY - fontMetrics.descent, paint);
        } else {
            canvas.drawText(mText, totalX, totalY - fontMetrics.descent, paint);// draw 从左上角开始，需要计算出需要的宽高
        }
    }
}
