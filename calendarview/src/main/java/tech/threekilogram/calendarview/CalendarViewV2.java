package tech.threekilogram.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Liujin 2019/2/21:12:04:54
 */
public class CalendarViewV2 extends ViewGroup {

      private static final String TAG = CalendarViewV2.class.getSimpleName();

      private View mWeekBar;
      private View mMonthLayout;
      private MeasureLayoutStrategy mLayoutStrategy;

      public CalendarViewV2 ( Context context ) {

            this( context, null, 0 );
      }

      public CalendarViewV2 ( Context context, AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public CalendarViewV2 ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init( context );
      }

      private void init ( Context context ) {

            mLayoutStrategy = new VerticalLinearMeasureLayoutStrategy();
            setWeekBar( new LinearWeekBar( context ) );
            setMonthLayout( new PagerMonthLayout( context ) );
      }

      public void setWeekBar ( View weekBar ) {

            if( mWeekBar != weekBar ) {
                  if( mWeekBar != null ) {
                        removeView( mWeekBar );
                  }
                  mWeekBar = weekBar;
                  addView( weekBar );
                  requestLayout();
            }
      }

      public void setMonthLayout ( View monthLayout ) {

            if( mMonthLayout != monthLayout ) {
                  if( mMonthLayout != null ) {
                        removeView( mMonthLayout );
                  }
                  mMonthLayout = monthLayout;
                  addView( mMonthLayout );
                  requestLayout();
            }
      }

      @Override
      public void measureChild (
          View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec ) {

            super.measureChild( child, parentWidthMeasureSpec, parentHeightMeasureSpec );
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthMode = MeasureSpec.getMode( widthMeasureSpec );
            int widthSize = MeasureSpec.getSize( widthMeasureSpec );

            int heightMode = MeasureSpec.getMode( heightMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();

            // 移除padding尺寸,构造尺寸spec
            widthMeasureSpec = MeasureSpec
                .makeMeasureSpec( widthSize - paddingLeft - paddingRight, widthMode );
            heightMeasureSpec = MeasureSpec
                .makeMeasureSpec( heightSize - paddingTop - paddingBottom, heightMode );

            // 使用策略测量组件
            int[] result = mLayoutStrategy.measureComponent(
                this,
                widthMeasureSpec, heightMeasureSpec,
                mWeekBar, mMonthLayout
            );

            // 设置尺寸信息
            int finalWidth = 0;
            int finalHeight = 0;

            if( widthMode == MeasureSpec.EXACTLY ) {

                  finalWidth = widthSize;
            } else if( widthMode == MeasureSpec.AT_MOST ) {

                  finalWidth = Math.min( ( result[ 0 ] + paddingLeft + paddingRight ), widthSize );
            } else {

                  finalWidth = widthSize;
            }

            if( heightMode == MeasureSpec.EXACTLY ) {

                  finalHeight = heightSize;
            } else if( widthMode == MeasureSpec.AT_MOST ) {

                  finalHeight = Math
                      .min( ( result[ 1 ] + paddingTop + paddingBottom ), heightSize );
            } else {

                  finalHeight = result[ 1 ];
            }

            setMeasuredDimension( finalWidth, finalHeight );
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            mLayoutStrategy.layoutComponent( this, mWeekBar, mMonthLayout );
      }

      /**
       * calendar布局策略
       */
      public interface MeasureLayoutStrategy {

            /**
             * 测量组件
             *
             * @param widthMeasureSpec 宽度
             * @param heightMeasureSpec 高度
             * @param weekBar 星期条组件
             * @param monthLayout 显示月份每一天的组件
             *
             * @return 布局占用的尺寸, int[0]表示宽度, int[1]表示高度
             */
            int[] measureComponent (
                CalendarViewV2 parent,
                int widthMeasureSpec,
                int heightMeasureSpec,
                View weekBar,
                View monthLayout
            );

            void layoutComponent (
                CalendarViewV2 parent, View weekBar, View monthLayout );
      }

      /**
       * 默认布局策略
       */
      protected class VerticalLinearMeasureLayoutStrategy implements MeasureLayoutStrategy {

            private int[] mResult = new int[ 2 ];

            @Override
            public int[] measureComponent (
                CalendarViewV2 parent,
                int widthMeasureSpec,
                int heightMeasureSpec,
                View weekBar,
                View monthLayout ) {

                  int widthSize = MeasureSpec.getSize( widthMeasureSpec );
                  int heightSize = MeasureSpec.getSize( heightMeasureSpec );

                  int widthExactSpec = MeasureSpec
                      .makeMeasureSpec( widthSize, MeasureSpec.EXACTLY );

                  // weekBar 高度包裹住自己
                  int weekBarHeightSpec = MeasureSpec
                      .makeMeasureSpec( heightSize, MeasureSpec.AT_MOST );
                  parent.measureChild( weekBar, widthExactSpec, weekBarHeightSpec );
                  int weekBarMeasuredHeight = weekBar.getMeasuredHeight();

                  // monthLayout 高度包裹住自己
                  int leftHeight = heightSize - weekBarMeasuredHeight;
                  int monthHeightSpec = MeasureSpec.makeMeasureSpec(
                      leftHeight,
                      MeasureSpec.AT_MOST
                  );
                  parent.measureChild( monthLayout, widthMeasureSpec, monthHeightSpec );

                  // 宽度是calendar宽度,高度是两个组件之和
                  mResult[ 0 ] = widthSize;
                  mResult[ 1 ] = weekBarMeasuredHeight + monthLayout.getMeasuredHeight();

                  return mResult;
            }

            @Override
            public void layoutComponent (
                CalendarViewV2 parent,
                View weekBar,
                View monthLayout ) {

                  int paddingLeft = parent.getPaddingLeft();
                  int paddingTop = parent.getPaddingTop();

                  int offset = paddingTop + weekBar.getMeasuredHeight();
                  weekBar.layout(
                      paddingLeft,
                      paddingTop,
                      paddingLeft + weekBar.getMeasuredWidth(),
                      offset
                  );

                  monthLayout.layout(
                      paddingLeft,
                      offset,
                      paddingLeft + monthLayout.getMeasuredWidth(),
                      offset + monthLayout.getMeasuredHeight()
                  );
            }
      }
}
