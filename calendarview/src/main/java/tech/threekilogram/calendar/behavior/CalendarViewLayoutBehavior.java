package tech.threekilogram.calendar.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import tech.threekilogram.calendar.CalendarView;

/**
 * @author Liujin 2019/3/11:11:17:22
 */
public class CalendarViewLayoutBehavior extends Behavior {

      private static final String TAG = CalendarViewLayoutBehavior.class.getSimpleName();

      private CalendarView mDependency;

      public CalendarViewLayoutBehavior ( ) { }

      public CalendarViewLayoutBehavior ( Context context, AttributeSet attrs ) {

            super( context, attrs );
      }

      @Override
      public boolean layoutDependsOn ( @NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency ) {

            Log.i( TAG, "layoutDependsOn: " );
            boolean b = dependency instanceof CalendarView;
            if( b ) {
                  mDependency = (CalendarView) dependency;
            }
            return b;
      }

      @Override
      public boolean onLayoutChild (
          @NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection ) {

            Log.i( TAG, "onLayoutChild: " );
            return super.onLayoutChild( parent, child, layoutDirection );
      }
}
