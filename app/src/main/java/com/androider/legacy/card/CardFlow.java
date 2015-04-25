package com.androider.legacy.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;
import android.widget.Toast;

import com.androider.legacy.R;

/**
 * Created by bao on 2015/4/4.
 */
public class CardFlow extends LinearLayout {

    private static final String TAG = "CardFlow";

    private final Runnable mBitmapManagerRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    private TextView mHeaderView;
    private TextView mTailView;
    private OnCardFlowScrollChangedListener mOnCardFlowScrollChangedListener;

    private int mBackgroundColor;

    private int mCardMargin = 0;

    private OverScroller mOverScroller;
    private VelocityTracker mVelocityTracker;
    private int mLastMotionY;
    private int mMinimumVelocity;
    private int mMaxmumVelocity;

    public CardFlow(Context context) {
        super(context);
        init();
    }

    public CardFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardFlow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {


        // 初始化背景色和卡片的间隔
        mBackgroundColor = getContext().getResources().getColor(R.color.cardflow_background_color);
        mCardMargin = getContext().getResources().getDimensionPixelSize(R.dimen.cardflow_card_margin);
        // 解决不调用onDraw()方法
        setWillNotDraw(false);

        initHeaderView();
        initTailView();
        // 初始化overScroller
        setOrientation(VERTICAL);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        mOverScroller = new OverScroller(getContext());
        obtainVelocityTracker();
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMaxmumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();

    }

    private void obtainVelocityTracker() {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    public void setOnCardFlowScrollChangedListener(OnCardFlowScrollChangedListener l) {
        mOnCardFlowScrollChangedListener = l;
    }

    private void cecycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void initHeaderView() {
        if (null == mHeaderView) {
            mHeaderView = new TextView(getContext());
            mHeaderView.setText("正在加载");
            mHeaderView.setVisibility(View.INVISIBLE);
            mHeaderView.setGravity(Gravity.CENTER);
            addCardView(mHeaderView, 0);
        }
    }

    public void initTailView() {
        if (null == mTailView) {
            mTailView = new TextView(getContext());
            mTailView.setText("正在加载");
            mTailView.setVisibility(View.INVISIBLE);
            mTailView.setGravity(Gravity.CENTER);
            addCardView(mTailView, 1);
        }
    }

    public void addCardView(View cardView, int index) {

        this.addView(cardView, index);
        ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
        if (layoutParams instanceof MarginLayoutParams) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams)layoutParams;
            marginLayoutParams.topMargin = mCardMargin;
            marginLayoutParams.leftMargin = mCardMargin;
            marginLayoutParams.rightMargin = mCardMargin;
            marginLayoutParams.bottomMargin = mCardMargin;
        }
    }

    public void addCardViewInHead(View cardView) {
        if (null == mHeaderView) {
            addCardView(cardView, 0);
        } else {
            addCardView(cardView, 1);
        }
    }

    public void addCardInTail(View cardView) {
        if (null == mTailView) {
            int count = getChildCount();
            addCardView(cardView, count);
        } else {
            int count = getChildCount();
            addCardView(cardView, count - 1);
        }
    }

    public boolean isChildShow(View child) {
        if (null == child && child.getParent() != this) {
            return false;
        } else if(child.getVisibility() == View.GONE) {
            return false;
        } else {
            int screenTop = getScrollY();
            int screenBottom = screenTop + getContentHeight();
            if (child.getTop() < screenBottom && child.getBottom() > screenTop) {
                return true;
            } else {
                return false;
            }
        }
    }

    public int getContentHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public void removeCardView(CardView cardView) {
        if (cardView != null && cardView.getParent() == this) {
            removeView(cardView);
        }
    }

    private int getChildBottom() {
        return mTailView.getBottom() + mCardMargin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 这个将高度变成所需要的高度
        int height = MeasureSpec.getSize(heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), height);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        // 如果还在滚动
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            postInvalidate();
        }
    }

    public void setTailViewVisible(int visible) {
        if (null != mTailView) {
            mTailView.setVisibility(visible);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (isChildShow(mTailView)) {
            setTailViewVisible(VISIBLE);
            if (mOnCardFlowScrollChangedListener.reqeustViews(this)) {
                setTailViewVisible(INVISIBLE);
            } else {
                Toast.makeText(getContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                setTailViewVisible(INVISIBLE);
            }
        }

    }

    public boolean isTop() {
        return getScaleY() <= 0;
    }

    public void completeMove(float velocityY) {

        int maxY = getChildBottom() - getHeight();
        int scrollyY = getScrollY();
        if (scrollyY > maxY) {
            mOverScroller.startScroll(0, scrollyY, 0, maxY - scrollyY);
            postInvalidate();
        } else if (scrollyY < 0) {
            mOverScroller.startScroll(0, scrollyY, 0, -scrollyY);
            postInvalidate();
        } else if (Math.abs(velocityY) >= mMinimumVelocity && maxY > 0) {
            mOverScroller.fling(0, getScrollY(), 0, (int)velocityY, 0, 0, 0, maxY);
            postInvalidate();
        }
//        Logger.getInstance().error(TAG, "ACTION_UP height is : " + height + "; bottom is " + bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);

        obtainVelocityTracker();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (getChildCount() <= 0) {
                    return false;
                }
                // 如果当前界面还在滚动，则告诉父节点不要拦截点击事件
                if(!mOverScroller.isFinished()) {
                    // 停止之前的操作
                    mOverScroller.abortAnimation();
                }

                mLastMotionY = (int)event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int y = (int)event.getY();
                if (y != mLastMotionY) {
                    if (getScaleY() < (getChildBottom() - getHeight())) {
//                        Logger.getInstance().error(TAG, "onTouchEvent_ACTION_MOVE; currentY is " + y + "; mLast is " + mLastMotionY);
                        scrollBy(0, - (y - mLastMotionY));
                        mLastMotionY = y;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mVelocityTracker.computeCurrentVelocity(1000, mMaxmumVelocity);
                float velocityY = mVelocityTracker.getYVelocity();
                completeMove(-velocityY);
                cecycleVelocityTracker();
            }
        }
        return true;
    }
}
