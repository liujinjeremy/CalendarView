package tech.threekilogram.calendarviewlib;

import android.graphics.Color;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
import tech.threekilogram.calendar.CalendarView;
import tech.threekilogram.calendar.month.MonthLayout;
import tech.threekilogram.calendar.month.MonthLayout.OnCurrentPageExpandFoldStrategy;
import tech.threekilogram.calendar.month.MonthLayout.OnPagerScrollHeightChangeStrategy;

/**
 * @author Liujin 2019/3/14:8:58:34
 */
public class CalendarViewCoverBehavior<T extends View> extends Behavior<T> {

      private CalendarView mCalendarView;
      private T            mCover;

      /**
       * 使用{@link CoordinatorLayout}的两个直接子view(一个是 {@link CalendarView},另一个用来遮盖 ),
       * 构建一个协调行为,在{@link CalendarView}界面变化时跟随变化
       *
       * @param layout 协调布局
       * @param calendarView 需要用来被遮盖的calendar
       * @param view 用来遮盖的view,需要布局在{@link CalendarView}后面
       */
      public CalendarViewCoverBehavior ( CoordinatorLayout layout, CalendarView calendarView, T view ) {

            int count = layout.getChildCount();
            int calendarIndex = 0;
            int coverIndex = 0;
            for( int i = 0; i < count; i++ ) {
                  View child = layout.getChildAt( i );
                  if( child == calendarView ) {
                        calendarIndex = i;
                  } else if( child == view ) {
                        coverIndex = i;
                  }
            }

            if( coverIndex < calendarIndex ) {
                  layout.removeViewAt( coverIndex );
                  layout.addView( view );
            }

            mCalendarView = calendarView;
            mCover = view;

            calendarView.setBackgroundColor( Color.LTGRAY );

            ( (LayoutParams) mCover.getLayoutParams() ).setBehavior( this );

            MonthLayout monthLayout = mCalendarView.getMonthLayout();
            monthLayout.setOnPagerScrollHeightChangeStrategy( new PagerScrollStrategy() );
            monthLayout.setOnCurrentPageExpandFoldStrategy( new PageExpandFoldStrategy() );
      }

      @Override
      public boolean onLayoutChild (
          @NonNull CoordinatorLayout parent, @NonNull T child, int layoutDirection ) {

            int calendarViewBottom = mCalendarView.getBottom();
            mCover.layout( 0, calendarViewBottom, mCover.getMeasuredWidth(),
                           calendarViewBottom + mCover.getMeasuredHeight()
            );

            return true;
      }

      private class PagerScrollStrategy implements OnPagerScrollHeightChangeStrategy {

            @Override
            public boolean onHeightChange ( int currentPageHeight, int targetPageHeight, float offset, int calculateHeight ) {

                  return false;
            }

            @Override
            public boolean onMeasureWhenScrolling ( int parentWidthSpec, int parentHeightSpec ) {

                  return false;
            }

            @Override
            public boolean onLayoutWhenScrolling ( int parentLeft, int parentTop, int parentRight, int parentBottom ) {

                  return false;
            }
      }

      private class PageExpandFoldStrategy implements OnCurrentPageExpandFoldStrategy {

            @Override
            public boolean onCurrentPageHeightChange ( int currentPageHeight ) {

                  return false;
            }

            @Override
            public boolean onMeasureWhenCurrentPageExpandFold ( int parentWidthSpec, int parentHeightSpec ) {

                  return false;
            }

            @Override
            public boolean onLayoutWhenCurrentPageExpandFold (
                int parentLeft, int parentTop, int parentRight, int parentBottom ) {

                  return false;
            }
      }
}
