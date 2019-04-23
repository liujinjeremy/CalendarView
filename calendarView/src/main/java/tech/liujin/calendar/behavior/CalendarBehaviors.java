package tech.liujin.calendar.behavior;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import tech.liujin.calendar.CalendarView;
import tech.liujin.calendar.month.MonthLayout;
import tech.liujin.calendar.month.MonthLayout.PageHeightChangeStrategy;
/**
 * @author Liujin 2019/3/11:15:37:33
 */
public class CalendarBehaviors {

      private CalendarView mCalendarView;
      private MonthLayout  mMonthLayout;
      private RecyclerView mRecyclerView;

      private int mCalendarBottomMargin;
      private int mRecyclerTopMargin;

      private boolean mFlagMeasure;
      private boolean mFlagLayout;

      /**
       * 为{@link CoordinatorLayout}的直接子view辅助创建{@link Behavior}
       *
       * @param calendarView 直接子view之一,当recycler竖直嵌套滑动时,会响应该滑动
       * @param recyclerView 直接子view之一
       */
      public void setUpWith ( CalendarView calendarView, RecyclerView recyclerView ) {

            mCalendarView = calendarView;
            mRecyclerView = recyclerView;

            /* calendar set behavior */
            LayoutParams layoutParams = ( (LayoutParams) calendarView.getLayoutParams() );
            CalendarBehavior calendarBehavior = new CalendarBehavior();
            layoutParams.setBehavior( calendarBehavior );

            /* set recycler behavior */
            layoutParams = (LayoutParams) recyclerView.getLayoutParams();
            RecyclerBehavior recyclerBehavior = new RecyclerBehavior();
            layoutParams.setBehavior( recyclerBehavior );

            /* recycler follow calendar */
            mMonthLayout = mCalendarView.getMonthLayout();
            mMonthLayout.setPageHeightChangeStrategy( new HeightChangeStrategy() );
      }

      /**
       * 测量recyclerView
       */
      private void measureRecycler ( int parentWidthMeasureSpec, int parentHeightMeasureSpec ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mRecyclerView.getLayoutParams();
            mRecyclerTopMargin = layoutParams.topMargin;

            int width;
            if( layoutParams.width > 0 ) {
                  width = layoutParams.width;
            } else {
                  width = MeasureSpec.getSize( parentWidthMeasureSpec )
                      - layoutParams.leftMargin - layoutParams.rightMargin;
            }
            int height;
            if( layoutParams.height > 0 ) {
                  height = layoutParams.height;
            } else {
                  MarginLayoutParams params = (MarginLayoutParams) mCalendarView.getLayoutParams();
                  int margin = params.topMargin + params.bottomMargin + layoutParams.topMargin + layoutParams.bottomMargin;
                  height = MeasureSpec.getSize( parentHeightMeasureSpec ) - mCalendarView.getMinimumHeight() - margin;
            }

            int childWidthSpec = MeasureSpec
                .makeMeasureSpec( width, MeasureSpec.EXACTLY );
            int childHeightSpec = MeasureSpec
                .makeMeasureSpec( height, MeasureSpec.EXACTLY );

            mRecyclerView.measure( childWidthSpec, childHeightSpec );
      }

      /**
       * 测量calendarView
       */
      private void measureCalendar ( int parentWidthMeasureSpec, int parentHeightMeasureSpec ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mCalendarView.getLayoutParams();
            mCalendarBottomMargin = layoutParams.bottomMargin;

            int width;
            if( layoutParams.width > 0 ) {
                  width = layoutParams.width;
            } else {
                  width =
                      MeasureSpec.getSize( parentWidthMeasureSpec ) - layoutParams.leftMargin - layoutParams.rightMargin;
            }
            int height;
            if( layoutParams.height > 0 ) {
                  height = layoutParams.height;
            } else {
                  height =
                      MeasureSpec.getSize( parentHeightMeasureSpec ) - layoutParams.topMargin - layoutParams.bottomMargin;
            }

            int childWidthSpec = MeasureSpec
                .makeMeasureSpec( width, MeasureSpec.EXACTLY );
            int childHeightSpec = MeasureSpec
                .makeMeasureSpec( height, MeasureSpec.AT_MOST );

            mCalendarView.measure( childWidthSpec, childHeightSpec );
      }

      /**
       * 布局recyclerView
       */
      private void layoutRecycler ( ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mRecyclerView.getLayoutParams();

            int l = layoutParams.leftMargin;
            int t = mCalendarView.getBottom()
                + ( (MarginLayoutParams) mCalendarView.getLayoutParams() ).bottomMargin
                + layoutParams.topMargin;
            int r = l + mRecyclerView.getMeasuredWidth();
            int b = t + mRecyclerView.getMeasuredHeight();

            mRecyclerView.layout( l, t, r, b );
      }

      /**
       * 布局calendarView
       */
      private void layoutCalendar ( ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mCalendarView.getLayoutParams();
            mCalendarView.layout(
                layoutParams.leftMargin,
                layoutParams.topMargin,
                layoutParams.leftMargin + mCalendarView.getMeasuredWidth(),
                layoutParams.topMargin + mCalendarView.getMeasuredHeight()
            );
      }

      /**
       * 同时测量calendar 和 recycler,如果已经测量过,那么跳过
       */
      private void onMeasure ( int parentWidthMeasureSpec, int parentHeightMeasureSpec ) {

            if( mFlagMeasure ) {
                  mFlagMeasure = false;
            } else {
                  mFlagMeasure = true;
                  measureCalendar( parentWidthMeasureSpec, parentHeightMeasureSpec );
                  measureRecycler( parentWidthMeasureSpec, parentHeightMeasureSpec );
            }
      }

      /**
       * 同时布局calendar 和 recycler,如果已经布局过,那么跳过
       */
      private void onLayout ( ) {

            if( mFlagLayout ) {
                  mFlagLayout = false;
            } else {
                  mFlagLayout = true;
                  layoutCalendar();
                  layoutRecycler();
            }
      }

      /**
       * {@link #mRecyclerView}的{@link Behavior}
       */
      private class RecyclerBehavior extends Behavior<RecyclerView> {

            /**
             * 用于滑动完毕后最后判断方向,是收缩还是展开
             */
            private int mLastDy;

            @Override
            public boolean onMeasureChild (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int parentWidthMeasureSpec, int widthUsed,
                int parentHeightMeasureSpec, int heightUsed ) {

                  onMeasure( parentWidthMeasureSpec, parentHeightMeasureSpec );
                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int layoutDirection ) {

                  onLayout();
                  return true;
            }

            @Override
            public boolean onStartNestedScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View directTargetChild,
                @NonNull View target, int axes, int type ) {

                  return ( axes & ViewCompat.SCROLL_AXIS_VERTICAL ) != 0;
            }

            @Override
            public void onNestedPreScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int dx, int dy,
                @NonNull int[] consumed, int type ) {

                  /* 已经折叠不响应滑动 */
                  if( mMonthLayout.isFolded() ) {
                        return;
                  }

                  /* 没有处于折叠 */
                  if( mMonthLayout.dispatchMoveToCurrentPage( -dy ) ) {
                        consumed[ 1 ] = dy;
                        mLastDy = -dy;
                  }
            }

            @Override
            public void onStopNestedScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int type ) {

                  super.onStopNestedScroll( coordinatorLayout, child, target, type );
                  release();
            }

            /**
             * 根据方向释放
             */
            private void release ( ) {

                  if( mLastDy != 0 ) {
                        mMonthLayout.dispatchReleaseToCurrentPage( mLastDy );
                        mLastDy = 0;
                  }
            }
      }

      /**
       * {@link #mCalendarView}的behavior
       */
      private class CalendarBehavior extends Behavior<CalendarView> {

            @Override
            public boolean onMeasureChild (
                @NonNull CoordinatorLayout parent, @NonNull CalendarView child, int parentWidthMeasureSpec, int widthUsed,
                int parentHeightMeasureSpec, int heightUsed ) {

                  onMeasure( parentWidthMeasureSpec, parentHeightMeasureSpec );
                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull CalendarView child, int layoutDirection ) {

                  onLayout();
                  return true;
            }
      }

      /**
       * 布局策略,当高度变化时同时改变recyclerView位置
       */
      private class HeightChangeStrategy implements PageHeightChangeStrategy {

            @Override
            public void onHeightChanging ( int currentHeight, int which ) {

                  mMonthLayout.reLayoutToPageHeight( currentHeight );
                  int bottom = mCalendarView.getBottom();
                  mRecyclerView.setY( bottom + mCalendarBottomMargin + mRecyclerTopMargin );
            }

            @Override
            public void onScrollFinished ( ) {

                  int bottom = mCalendarView.getBottom();
                  int y = bottom + mCalendarBottomMargin + mRecyclerTopMargin;
                  mRecyclerView.setY( y );
            }

            @Override
            public void onExpanded ( ) { }

            @Override
            public void onFolded ( ) { }
      }
}
