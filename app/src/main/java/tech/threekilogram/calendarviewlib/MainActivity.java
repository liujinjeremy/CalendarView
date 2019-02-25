package tech.threekilogram.calendarviewlib;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener;
import java.util.Date;
import tech.threekilogram.calendarview.CalendarUtils;
import tech.threekilogram.calendarview.CalendarView;
import tech.threekilogram.calendarview.month.PagerMonthLayout;

public class MainActivity extends AppCompatActivity {

      private static final String TAG = MainActivity.class.getSimpleName();

      private TextView     mTitle;
      private CalendarView mCalendarView;
      private FrameLayout  mRoot;

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );
            initView();
      }

      private void initView ( ) {

            mRoot = findViewById( R.id.root );
            mTitle = findViewById( R.id.title );
            mCalendarView = findViewById( R.id.calendarView );
            mCalendarView.setFirstDayMonday( true );

            final PagerMonthLayout monthLayout = (PagerMonthLayout) mCalendarView.getMonthLayout();
            monthLayout.post( new Runnable() {

                  @Override
                  public void run ( ) {

                        int currentItem = monthLayout.getCurrentItem();
                        Date date = monthLayout.getDate( currentItem );
                        String yearMonthFormat = CalendarUtils.getYearMonthFormat( date );
                        mTitle.setText( yearMonthFormat );
                  }
            } );

            monthLayout.addOnPageChangeListener( new SimpleOnPageChangeListener() {

                  @Override
                  public void onPageSelected ( int position ) {

                        super.onPageSelected( position );
                        Date date = monthLayout.getDate( position );
                        String format = CalendarUtils.getYearMonthFormat( date );
                        mTitle.setText( format );
                  }
            } );
      }
}
