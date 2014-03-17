/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.basilwang.ui.widget;

import net.basilwang.R;
import net.basilwang.utils.CurriculumUtils;
import net.basilwang.utils.DimentionUtils;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

/**
 * Custom layout that contains and organizes a {@link TimeRulerView} and several
 * instances of {@link BlockView}. Also positions current "now" divider using
 * {@link R.id#blocks_now} view when applicable.
 */
public class BlocksLayout extends ViewGroup {
    private int mColumns = 7;

    private TimeRulerView mRulerView;
    private FrameLayout mBlocksContainerView;
    private HorizontalScrollView mHorizontalScrollView;
    private View mNowView;
    private FrameLayout mWeekDayHeaderFrameLayout;

    public BlocksLayout(Context context) {	
        this(context, null);
        Log.v("test", "BlocksLayout Constructure");
    }

    public BlocksLayout(Context context, AttributeSet attrs) { 	
        this(context, attrs, 0);
        Log.v("test", "BlocksLayout Constructure");
    }

    public BlocksLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.v("test", "BlocksLayout Constructure");
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BlocksLayout, defStyle, 0);

        mColumns = a.getInt(R.styleable.TimeRulerView_headerWidth, mColumns);

        a.recycle();
    }

    private void ensureChildren() {
      
        if (mRulerView == null) {
        	  mRulerView = (TimeRulerView) findViewById(R.id.blocks_ruler);
              mRulerView.setDrawingCacheEnabled(true);
        }

       
        if (mNowView == null) {
        	 mNowView = findViewById(R.id.blocks_now);
             mNowView.setDrawingCacheEnabled(true);
        }
        
   
        if (mBlocksContainerView == null) {
            mBlocksContainerView =  (FrameLayout) findViewById(R.id.blocks_container);
            mBlocksContainerView.setDrawingCacheEnabled(true);
        }
        
        
        if (mHorizontalScrollView == null) {
        	mHorizontalScrollView =  (HorizontalScrollView) findViewById(R.id.horizontal_scroll);
        	mHorizontalScrollView.setDrawingCacheEnabled(true);
        }
        //2012-12-01 basilwang very bad smell. findViewById from context
        if (mWeekDayHeaderFrameLayout == null) {
        	mWeekDayHeaderFrameLayout =  (FrameLayout) ((Activity)this.getContext()).findViewById(R.id.week_day_container);
//        	mWeekDayHeaderFrameLayout.setDrawingCacheEnabled(true);
        }
    }

    /**
     * Remove any {@link BlockView} instances, leaving only
     * {@link TimeRulerView} remaining.
     */
    public void removeAllBlocks() {
        ensureChildren();
        mBlocksContainerView.removeAllViews();
        addView(mRulerView);
        addView(mNowView);
    }

    public void addBlock(BlockView blockView) {
    	ensureChildren();
        blockView.setDrawingCacheEnabled(true);
        mBlocksContainerView.addView(blockView, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureChildren();

        Log.v("onMeasure", "BlocksLayout  onMeasure");
        Log.v("onMeasure", "BlocksLayout's width is" + mRulerView.getMeasuredWidth() );
        mRulerView.measure(widthMeasureSpec, heightMeasureSpec);
        mWeekDayHeaderFrameLayout.measure(widthMeasureSpec, heightMeasureSpec);
       // mNowView.measure(widthMeasureSpec, heightMeasureSpec);
        Log.v("onMeasure", "BlocksLayout getHeight is "+getHeight());
       // mBlocksContainerView.measure(637, 440);
        Log.v("onMeasure", "BlocksLayout's width is" + DimentionUtils.getWindowWidth(this.getContext()) );
        //Log.v("onMeasure", "BlocksLayout's height is" + mRulerView.getMeasuredTimeRulerViewHeight());
        final int height = mRulerView.getMeasuredHeight();
        setMeasuredDimension(resolveSize(DimentionUtils.getWindowWidth(this.getContext()), widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }  
    
    @Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		Log.v("BlocksLayout", "BlocksLayout  onDraw");
        final int blockHeight = mRulerView.getBlockHeight();
        final int blockWidth=getBlocksWidth();

        final Paint dividerPaint  = new Paint();
        ;
        dividerPaint.setColor(Color.rgb(217, 217, 217));
        dividerPaint.setStyle(Style.FILL);

        final int right = getRight();
        Log.v("blocks layout right","blocks layout right is: " +right);
        final int classIndexCount = CurriculumUtils.getClassIndexCount();
        // Walk left side of canvas drawing timestamps
        //final int hours = mEndHour - mStartHour;
        for (int i = 0; i < classIndexCount; i++) {
            final int dividerY = blockHeight * i;
            canvas.drawLine(0, dividerY, right, dividerY, dividerPaint);          
        }
        Log.v("header width","header width is: " +mWeekDayHeaderFrameLayout.getWidth());
        Log.v("block width","block width is: " +getBlocksWidth());
        //mWeekDayHeaderFrameLayout.layout(0, 0, +getBlocksWidth()*mColumns, mRulerView.getWeekDayHeaderHeight());
	}

	@Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureChildren();
        Log.v("onLayout", "BlocksLayout onLayout");
        final TimeRulerView rulerView = mRulerView;
        final int headerWidth = rulerView.getHeaderWidth();
        Log.v("onLayout", "rulerView headerWidth is" + headerWidth);
        //final int columnWidth = (getWidth() - headerWidth) / mColumns;
        final int columnWidth = getBlocksWidth();
        Log.v("onLayout", "blockWidth is" + columnWidth);
        rulerView.layout(0, 0, mRulerView.getHeaderWidth(), getHeight());
        Log.v("onLayout", "BlocksLayout getHeight is "+getHeight());
        mHorizontalScrollView.layout(mRulerView.getHeaderWidth(),0, getWidth(), getHeight());
        //2012-12-01 basilwang don't know why we need minus mRulerView.getHeaderWidth(). 
        //2012-12-12 basilwang  left is 0 and width is columnWidth*mColumns. IMPORTANT!!
        mBlocksContainerView.layout(0,0,columnWidth*mColumns, getHeight());
        final int count = mBlocksContainerView.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = mBlocksContainerView.getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            if (child instanceof BlockView) {
                final BlockView blockView = (BlockView) child;
                final int top = rulerView.getTimeVerticalOffset(blockView.getStartTime());
                final int bottom = rulerView.getTimeVerticalOffset(blockView.getEndTime());
                //2012-12-12 basilwang don't need plus mRulerView.getHeaderWidth()
                final int left =  (blockView.getColumn() * columnWidth);
                final int right = left + columnWidth;
                //Log.v("onLayout", "top"+top+"bottom"+bottom+"left"+left+"right"+right);
                child.layout(left, top, right, bottom);
                
            }
        }
        //2012-12-1 basilwang don't forget to add headerWidth and we must layout mWeekDayHeaderFrameLayout and all its children seperately
        mWeekDayHeaderFrameLayout.layout(0, 0, +columnWidth*mColumns, rulerView.getWeekDayHeaderHeight());
        for (int i = 1; i <= mColumns; i++) {
            final View child = mWeekDayHeaderFrameLayout.getChildAt(i);

            if (child instanceof TextView) {
            	final TextView textView = (TextView) child;
                final int top = 0;
                final int bottom = rulerView.getWeekDayHeaderHeight();
                final int left = headerWidth + (i-1) * columnWidth;
                final int right = left + columnWidth;
                Log.v("onLayout", "top"+top+"bottom"+bottom+"left"+left+"right"+right);
                textView.setGravity(Gravity.CENTER);
                child.layout(left, top, right, bottom);
                
            }
        }
        mWeekDayHeaderFrameLayout.requestLayout();
        
 
//        // Align now view to match current time
//        final View nowView = mNowView;
//        final long now = System.currentTimeMillis();
//
//        final int top = rulerView.getTimeVerticalOffset(now);
//        final int bottom = top + nowView.getMeasuredHeight();
//        final int left = 0;
//        final int right = getWidth();
//
//        nowView.layout(left, top, right, bottom);
    }
    private int getBlocksWidth()
    {
    	int block=(DimentionUtils.getWindowWidth(this.getContext())-mRulerView.getHeaderWidth())/3;
        return block;
    }
//    private int getMeasuredBlocksContainerWidth()
//    {
//    	return getBlocksWidth()*mColumns;
//    }
    
}
