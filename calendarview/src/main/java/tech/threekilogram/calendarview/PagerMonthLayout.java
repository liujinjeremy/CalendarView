package tech.threekilogram.calendarview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.LinkedList;
import tech.threekilogram.calendarview.CalendarView.ViewComponent;

/**
 * @author Liujin 2019/2/21:13:00:25
 */
public class PagerMonthLayout extends ViewPager implements ViewComponent {

      private static final String TAG = PagerMonthLayout.class.getSimpleName();

      private CalendarView mParent;

      private int mYear;
      private int mMonth;

      public PagerMonthLayout ( @NonNull Context context, CalendarView parent ) {

            super( context );
            mParent = parent;
            init();
      }

      private void init ( ) {

            setAdapter( new PagerMonthAdapter() );
            setCurrentItem( Integer.MAX_VALUE >> 1 );
      }

      @Override
      public View getView ( ) {

            return this;
      }

      private class PagerMonthAdapter extends PagerAdapter {

            private LinkedList<View> mReUsed = new LinkedList<>();

            @Override
            public int getCount ( ) {

                  return Integer.MAX_VALUE;
            }

            @NonNull
            @Override
            public Object instantiateItem ( @NonNull ViewGroup container, int position ) {

                  MonthPage page;
                  if( mReUsed.isEmpty() ) {
                        page = new MonthPage( container.getContext() );
                  } else {
                        page = (MonthPage) mReUsed.pollFirst();
                  }

                  container.addView(
                      page,
                      new ViewGroup.LayoutParams(
                          ViewGroup.LayoutParams.MATCH_PARENT,
                          ViewGroup.LayoutParams.MATCH_PARENT
                      )
                  );
                  return page;
            }

            @Override
            public boolean isViewFromObject ( @NonNull View view, @NonNull Object object ) {

                  return object == view;
            }

            @Override
            public void destroyItem ( @NonNull ViewGroup container, int position, @NonNull Object object ) {

                  View view = (View) object;
                  container.removeView( view );
                  mReUsed.add( view );
            }
      }
}
