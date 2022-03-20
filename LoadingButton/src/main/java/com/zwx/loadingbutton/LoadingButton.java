package com.zwx.loadingbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class LoadingButton extends ConstraintLayout {

    //region Variables
    static final int DEFAULT_COLOR = Color.WHITE;
    public static final int IN_FROM_RIGHT = 0;
    public static final int IN_FROM_LEFT = 1;

    private static final int[] STATE_YES = {R.attr.pbYesStyle};
    private static final int[] STYLE_WEAK = {R.attr.pbWeakStyle};

    private int mDefaultTextSize;
    private ProgressBar mProgressBar;
    private TextSwitcher mTextSwitcher;
    private float mTextSize;
    private int mTextColor;
    private int mTextAppearence;
    private boolean mIsLoadingShowing;
    private Typeface mTypeface;
    private Animation inRight;
    private Animation inLeft;
    private int mCurrentInDirection;
    private boolean mTextSwitcherReady;
    private boolean mYes;
    private boolean mIsStyleWeak;
    private boolean mProgressBarLeftMode;

    private String mLoadingText;
    private String mButtonText;

    /**
     * Loading状态字符串res id
     */
    private int mLoadingTextId;

    /**
     * 正常状态字符串res id
     */
    private int mButtonTextId;

    /**
     * Loading状态字符串格式化参数
     */
    private Object[] mLoadingTextFormatArgs;

    /**
     * 正常状态字符串格式化参数
     */
    private Object[] mButtonTextFormatArgs;
    //endregion

    //region Constructors
    public LoadingButton(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    //endregion

    public float getTextSize() {
        return mTextSize;
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setProgressColor(int colorRes) {
        mProgressBar.getIndeterminateDrawable()
                .mutate()
                .setColorFilter(colorRes, PorterDuff.Mode.SRC_ATOP);
    }

    public void setTypeface(@NonNull Typeface typeface) {
        this.mTypeface = typeface;
    }

    public void setAnimationInDirection(int inDirection) {
        mCurrentInDirection = inDirection;
    }

    public void setProgressBarLeftMode() {
        mProgressBarLeftMode = true;
        reversehorizontally();
    }

    public void setText(String text) {
        if (text != null) {
            mButtonText = text;
            if (mTextSwitcherReady) {
                mTextSwitcher.setInAnimation(mCurrentInDirection == IN_FROM_LEFT ? inLeft : inRight);
                mTextSwitcher.setText(mButtonText);
            }
        }
    }

    /**
     * 设置正常状态字符串res id
     *
     * @param resId 正常状态字符串res id
     */
    public void setText(@StringRes int resId) {
        mButtonTextId = resId;
        mButtonTextFormatArgs = null;
        if (mTextSwitcherReady) {
            setTextSwitcherText(mButtonTextId);
        }
    }

    /**
     * 设置正常状态字符串res id
     *
     * @param resId 正常状态字符串res id
     * @param formatArgs 格式化参数
     */
    public void setText(@StringRes int resId, Object... formatArgs) {
        mButtonTextId = resId;
        mButtonTextFormatArgs = formatArgs;
        if (mTextSwitcherReady) {
            setTextSwitcherText(mButtonTextId, mButtonTextFormatArgs);
        }
    }

    private void setTextSwitcherText(String text, Object... formatArgs) {
        mTextSwitcher.setInAnimation(mCurrentInDirection == IN_FROM_LEFT ? inLeft : inRight);
        if (formatArgs != null && formatArgs.length > 0) {
            text = String.format(text, formatArgs);
        }
        mTextSwitcher.setText(text);
    }

    private void setTextSwitcherText(@StringRes int resId, Object... formatArgs) {
        if (resId == 0) {
            return;
        }
        String raw = getResources().getString(resId);
        setTextSwitcherText(raw, formatArgs);
    }

    public void setLoadingText(String text) {
        if (text != null) {
            mLoadingText = text;
        }
    }

    public void setTextFactory(@NonNull ViewSwitcherFactory factory) {
        mTextSwitcher.removeAllViewsInLayout();
        mTextSwitcher.setFactory(factory);
        mTextSwitcher.setText(mButtonText);
    }

    public void showLoading() {
        if (!mIsLoadingShowing) {
            mProgressBar.setVisibility(View.VISIBLE);
            setTextSwitcherText(mLoadingText, mLoadingTextFormatArgs);
            mIsLoadingShowing = true;
            setEnabled(false);
        }
    }

    public void showJustLoading() {
        if (!mIsLoadingShowing) {
            mProgressBar.setVisibility(View.VISIBLE);
            mIsLoadingShowing = true;
            setEnabled(false);
        }
    }

    public void hideLoading() {
        if (mIsLoadingShowing) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mTextSwitcher.setText(mButtonText);
            mIsLoadingShowing = false;
            setEnabled(true);
        }
    }

    public boolean isLoading() {
        return mIsLoadingShowing;
    }

    private void init(Context context, AttributeSet attrs) {
        mDefaultTextSize = getResources().getDimensionPixelSize(R.dimen.text_default_size);
        mIsLoadingShowing = false;
        LayoutInflater.from(getContext()).inflate(R.layout.view_loading_button, this, true);

        mProgressBar = findViewById(R.id.pb_progress);
        mTextSwitcher = findViewById(R.id.pb_text);


        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.LoadingButton,
                    0, 0);
            try {
                float textSize = a.getDimensionPixelSize(R.styleable.LoadingButton_pbTextSize, mDefaultTextSize);
                setTextSize(textSize);

                String text = a.getString(R.styleable.LoadingButton_pbText);
                mButtonTextId = a.getResourceId(R.styleable.LoadingButton_pbText, 0);
                setText(text);

                mLoadingTextId = a.getResourceId(R.styleable.LoadingButton_pbLoadingText, R.string.default_loading);
                mLoadingText = a.getString(R.styleable.LoadingButton_pbLoadingText);

                if (mLoadingText == null) {
                    mLoadingText = getContext().getString(R.string.default_loading);
                }

                Log.d("LoadingButton", "mLoadingTextResId: " + mLoadingTextId);
                Log.d("LoadingButton", "R.string.loading: " + R.string.default_loading);
                Log.d("LoadingButton", "mLoadingText: " + mLoadingText);
                Drawable indeterminateDrawable = a.getDrawable(R.styleable.LoadingButton_pbIndeterminateDrawable);
                if (indeterminateDrawable != null) {
                    mProgressBar.setIndeterminateDrawable(indeterminateDrawable);
                }

                int progressColor = a.getColor(R.styleable.LoadingButton_pbProgressColor, DEFAULT_COLOR);
                setProgressColor(progressColor);

                int textColor = a.getColor(R.styleable.LoadingButton_pbTextColor, DEFAULT_COLOR);
                setTextColor(textColor);

                mYes = a.getBoolean(R.styleable.LoadingButton_pbYesStyle, true);
                mIsStyleWeak = a.getBoolean(R.styleable.LoadingButton_pbWeakStyle, false);
                mProgressBarLeftMode = a.getBoolean(R.styleable.LoadingButton_pbProgressLeft, false);
                mTextAppearence = a.getResourceId(R.styleable.LoadingButton_pbTextAppearence, -1);
            } finally {
                a.recycle();
            }
        } else {
            int white = Color.WHITE;
            mLoadingText = getContext().getString(R.string.default_loading);
            setProgressColor(white);
            setTextColor(white);
            setTextSize(mDefaultTextSize);
        }
        if (mProgressBarLeftMode) {
            reversehorizontally();
        }
        setupTextSwitcher();
    }

    private void reversehorizontally() {
        ConstraintSet cs = new ConstraintSet();
        cs.clone(this);
        int pgId = mProgressBar.getId();
        int tsId = mTextSwitcher.getId();
        int pId = ConstraintLayout.LayoutParams.PARENT_ID;

        cs.connect(pgId, ConstraintSet.TOP, tsId, ConstraintSet.TOP);
        cs.connect(pgId, ConstraintSet.BOTTOM, tsId, ConstraintSet.BOTTOM);
        cs.connect(pgId, ConstraintSet.END, tsId, ConstraintSet.START);
        cs.connect(pgId, ConstraintSet.START, pId, ConstraintSet.START);

        cs.connect(tsId, ConstraintSet.TOP, pId, ConstraintSet.TOP);
        cs.connect(tsId, ConstraintSet.BOTTOM, pId, ConstraintSet.BOTTOM);
        cs.connect(tsId, ConstraintSet.END, pId, ConstraintSet.END);
        cs.connect(tsId, ConstraintSet.START, pId, ConstraintSet.START);

        cs.setHorizontalBias(pgId, 1);

        cs.applyTo(this);
        TransitionManager.beginDelayedTransition(this);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if (!this.mYes) {
            return super.onCreateDrawableState(extraSpace);
        }
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mIsStyleWeak) {
            mergeDrawableStates(drawableState, STYLE_WEAK);
        } else {
            mergeDrawableStates(drawableState, STATE_YES);
        }
        return drawableState;
    }

    public void setYes(boolean mYes) {
        if (mYes != this.mYes) {
            this.mYes = mYes;
            refreshDrawableState();
        }
    }

    public boolean isYes() {
        return mYes;
    }

    private void setupTextSwitcher() {
        ViewSwitcherFactory factory = new ViewSwitcherFactory(getContext(), mTextColor, mTextSize, mTypeface, mTextAppearence);
        mTextSwitcher.setFactory(factory);

        inRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        inLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        mTextSwitcher.setOutAnimation(out);
        mTextSwitcher.setInAnimation(inRight);

        mTextSwitcher.setText(mButtonText);
        mTextSwitcherReady = true;

        // setup progress bar size
        setupProgressbarSize(factory);
    }

    /**
     * progressbar 和文字等高度；和文字间距为高度的一半
     */
    private void setupProgressbarSize(ViewSwitcherFactory factory) {
        TextView tv = (TextView) factory.makeView();
        float textSize = tv.getTextSize();
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mProgressBar.getLayoutParams();
        layoutParams.width = (int) textSize;
        layoutParams.height = (int) textSize;
        if (mProgressBarLeftMode) {
            layoutParams.rightMargin = (int) (textSize / 2);
        } else {
            layoutParams.leftMargin = (int) (textSize / 2);
        }
        mProgressBar.setLayoutParams(layoutParams);
    }

    private void setTextSize(float size) {
        mTextSize = size;
    }

    private void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public void setTextAppearence(int resId) {
        if (resId != this.mTextAppearence) {
            this.mTextAppearence = resId;
            invalidate();
        }
    }

    public static class ViewSwitcherFactory implements ViewSwitcher.ViewFactory {

        //region Variables
        private final int textColor;
        private final float textSize;
        private final Typeface typeFace;
        private final int textAppearence;
        private final Context context;
        //endregion

        //region Constructor
        public ViewSwitcherFactory(Context context, int textColor, float textSize, Typeface typeface, int textAppearence) {
            this.context = context;
            this.textColor = textColor;
            this.textSize = textSize;
            this.typeFace = typeface;
            this.textAppearence = textAppearence;
        }
        //endregion

        @Override
        public View makeView() {
            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(typeFace);
            if (textAppearence != -1) {
                tv.setTextAppearance(context, textAppearence);
            } else {
                tv.setTextColor(textColor);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            return tv;
        }
    }
}