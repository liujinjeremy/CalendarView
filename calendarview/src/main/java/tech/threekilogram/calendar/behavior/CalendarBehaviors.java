package tech.threekilogram.calendar.behavior;

import android.graphics.Color;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import tech.threekilogram.calendar.CalendarView;

/**
 * @author Liujin 2019/3/11:15:37:33
 */
public class CalendarBehaviors {

      private static final String TAG = CalendarBehaviors.class.getSimpleName();

      private RecyclerView mRecyclerView;
      private CalendarView mCalendarView;
      private RecyclerBehavior mRecyclerBehavior;
      private CalendarBehavior mCalendarBehavior;

      /**
       * 为{@link CoordinatorLayout}的直接子view辅助创建{@link Behavior}
       *
       * @param calendarView 直接子view之一,当recycler竖直嵌套滑动时,会响应该滑动
       * @param recyclerView 直接子view之一
       */
      public void setUp ( CalendarView calendarView, RecyclerView recyclerView ) {

            mCalendarView = calendarView;
            mCalendarView.setBackgroundColor( Color.LTGRAY );
            mRecyclerView = recyclerView;

            /* set recycler behavior */
            LayoutParams layoutParams = (LayoutParams) recyclerView.getLayoutParams();
            mRecyclerBehavior = new RecyclerBehavior();
            layoutParams.setBehavior( mRecyclerBehavior );

            /* calendar set behavior */
            layoutParams = ( (LayoutParams) calendarView.getLayoutParams() );
            mCalendarBehavior = new CalendarBehavior();
            layoutParams.setBehavior( mCalendarBehavior );
      }

      /**
       * {@link #mRecyclerView}的{@link Behavior}
       */
      private class RecyclerBehavior extends Behavior<RecyclerView> {

            @Override
            public boolean onMeasureChild (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int parentWidthMeasureSpec, int widthUsed,
                int parentHeightMeasureSpec, int heightUsed ) {

                  int width = MeasureSpec.getSize( parentWidthMeasureSpec );
                  int height = MeasureSpec.getSize( parentHeightMeasureSpec );
                  String format = String.format( "%d %d - %d %d", width, height, widthUsed, heightUsed );
                  Log.i( TAG, "onMeasureChild: " + format );

                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int layoutDirection ) {

                  Log.i( TAG, "onLayoutChild: " + child );
                  return true;
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

                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                  int width;
                  if( layoutParams.width > 0 ) {
                        width = layoutParams.width;
                  } else {
                        width = MeasureSpec.getSize( parentWidthMeasureSpec );
                  }
                  int height;
                  if( layoutParams.height > 0 ) {
                        height = layoutParams.height;
                  } else {
                        height = MeasureSpec.getSize( parentHeightMeasureSpec );
                  }

                  int childWidthSpec = MeasureSpec
                      .makeMeasureSpec( width - layoutParams.leftMargin - layoutParams.rightMargin, MeasureSpec.EXACTLY );
                  int childHeightSpec = MeasureSpec
                      .makeMeasureSpec( height - layoutParams.topMargin - layoutParams.bottomMargin, MeasureSpec.EXACTLY );

                  child.measure( childWidthSpec, childHeightSpec );
                  Log.i( TAG, "onMeasureChild: " );
                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull CalendarView child, int layoutDirection ) {

                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                  child.layout( layoutParams.leftMargin, layoutParams.topMargin,
                                layoutParams.leftMargin + child.getMeasuredWidth(),
                                layoutParams.topMargin + child.getMeasuredHeight()
                  );
                  Log.i( TAG, "onLayoutChild: " );
                  return true;
            }
      }

      /**
       * {@link #mRecyclerView}的滚动监听,用于记录一共滚动了多少距离
       */
      private class RecyclerScrollListener extends OnScrollListener {

            private int mState;
            private int mMoved;

            @Override
            public void onScrollStateChanged ( @NonNull RecyclerView recyclerView, int newState ) {

                  mState = newState;
            }

            @Override
            public void onScrolled ( @NonNull RecyclerView recyclerView, int dx, int dy ) {

                  mMoved += dy;
            }
      }
}
