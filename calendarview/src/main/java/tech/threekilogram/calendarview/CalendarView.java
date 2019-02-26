package tech.threekilogram.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.Date;
import tech.threekilogram.calendarview.month.MonthLayout;
import tech.threekilogram.calendarview.week.LinearWeekBar;

/**
 * @author Liujin 2019/2/21:12:04:54
 */
public class CalendarView extends ViewGroup {

      private static final String TAG = CalendarView.class.getSimpleName();

      /**
       * 头部星期条
       */
      private ViewComponent         mWeekBar;
      /**
       * 月视图
       */
      private ViewComponent         mMonthLayout;
      /**
       * 布局策略
       */
      private MeasureLayoutStrategy mLayoutStrategy;
      /**
       * 每周的起始是不是周一
       */
      private boolean               isFirstDayMonday = true;

      /**
       * 起始日期
       */
      private Date mDate = new Date();

      public CalendarView ( Context context ) {

            this( context, null, 0 );
      }

      public CalendarView ( Context context, AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public CalendarView ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init( context );
      }

      private void init ( Context context ) {

            mLayoutStrategy = new VerticalLinearMeasureLayoutStrategy();

            MonthLayout monthLayout = new MonthLayout( context );
            setMonthLayout( monthLayout );

            LinearWeekBar weekBar = new LinearWeekBar( context );
            setWeekBar( weekBar );
      }

      public void setDate ( Date date ) {

            mDate = date;
      }

      public Date getDate ( ) {

            return mDate;
      }

      public void setWeekBar ( ViewComponent weekBar ) {

            if( mWeekBar != weekBar ) {
                  if( mWeekBar != null ) {
                        removeView( mWeekBar.getView() );
                  }
                  mWeekBar = weekBar;
                  weekBar.bindParent( this );
                  addView( weekBar.getView() );
                  requestLayout();
            }
      }

      public ViewComponent getWeekBar ( ) {

            return mWeekBar;
      }

      public void setMonthLayout ( ViewComponent monthLayout ) {

            if( mMonthLayout != monthLayout ) {
                  if( mMonthLayout != null ) {
                        removeView( mMonthLayout.getView() );
                  }
                  mMonthLayout = monthLayout;
                  monthLayout.bindParent( this );
                  addView( monthLayout.getView() );
                  requestLayout();
            }
      }

      public ViewComponent getMonthLayout ( ) {

            return mMonthLayout;
      }

      public void setFirstDayMonday ( boolean firstDayMonday ) {

            if( isFirstDayMonday != firstDayMonday ) {

                  isFirstDayMonday = firstDayMonday;
                  mWeekBar.notifyFirstDayIsMondayChanged( firstDayMonday );
                  mMonthLayout.notifyFirstDayIsMondayChanged( firstDayMonday );
            }
      }

      public boolean isFirstDayMonday ( ) {

            return isFirstDayMonday;
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
       * calendar的view组件
       */
      public interface ViewComponent {

            /**
             * 获取组件
             *
             * @return view组件
             */
            View getView ( );

            /**
             * 绑定父组件
             *
             * @param calendarView 父组件
             */
            void bindParent ( CalendarView calendarView );

            /**
             * 通知每周的第一天是否是周一改变了
             *
             * @param isFirstDayMonday 每周第一天是否是周一
             */
            void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday );
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
                CalendarView parent,
                int widthMeasureSpec,
                int heightMeasureSpec,
                ViewComponent weekBar,
                ViewComponent monthLayout
            );

            void layoutComponent (
                CalendarView parent, ViewComponent weekBar, ViewComponent monthLayout );
      }

      /**
       * 默认布局策略
       */
      protected class VerticalLinearMeasureLayoutStrategy implements MeasureLayoutStrategy {

            private int[] mResult = new int[ 2 ];

            @Override
            public int[] measureComponent (
                CalendarView parent,
                int widthMeasureSpec,
                int heightMeasureSpec,
                ViewComponent weekBar,
                ViewComponent monthLayout ) {

                  int widthSize = MeasureSpec.getSize( widthMeasureSpec );
                  int heightSize = MeasureSpec.getSize( heightMeasureSpec );

                  int widthExactSpec = MeasureSpec
                      .makeMeasureSpec( widthSize, MeasureSpec.EXACTLY );

                  // weekBar 高度包裹住自己
                  int weekBarHeightSpec = MeasureSpec
                      .makeMeasureSpec( heightSize, MeasureSpec.AT_MOST );
                  View view = weekBar.getView();
                  view.measure( widthExactSpec, weekBarHeightSpec );
                  int weekBarMeasuredHeight = view.getMeasuredHeight();

                  // monthLayout 高度包裹住自己
                  int leftHeight = heightSize - weekBarMeasuredHeight;
                  int monthHeightSpec = MeasureSpec.makeMeasureSpec(
                      leftHeight,
                      MeasureSpec.AT_MOST
                  );
                  View layoutView = monthLayout.getView();
                  layoutView.measure( widthExactSpec, monthHeightSpec );

                  // 宽度是calendar宽度,高度是两个组件之和
                  mResult[ 0 ] = widthSize;
                  mResult[ 1 ] = weekBarMeasuredHeight + layoutView.getMeasuredHeight();

                  return mResult;
            }

            @Override
            public void layoutComponent (
                CalendarView parent,
                ViewComponent weekBar,
                ViewComponent monthLayout ) {

                  int paddingLeft = parent.getPaddingLeft();
                  int paddingTop = parent.getPaddingTop();

                  View view = weekBar.getView();
                  int offset = paddingTop + view.getMeasuredHeight();
                  view.layout(
                      paddingLeft,
                      paddingTop,
                      paddingLeft + view.getMeasuredWidth(),
                      offset
                  );

                  View layoutView = monthLayout.getView();
                  layoutView.layout(
                      paddingLeft,
                      offset,
                      paddingLeft + layoutView.getMeasuredWidth(),
                      offset + layoutView.getMeasuredHeight()
                  );
            }
      }
}
