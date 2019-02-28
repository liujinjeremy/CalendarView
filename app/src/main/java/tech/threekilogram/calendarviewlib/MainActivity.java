package tech.threekilogram.calendarviewlib;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import tech.threekilogram.calendarview.CalendarUtils;
import tech.threekilogram.calendarview.CalendarView;
import tech.threekilogram.calendarview.month.MonthPage;

public class MainActivity extends AppCompatActivity {

      private static final String TAG = MainActivity.class.getSimpleName();

      private TextView     mTitle;
      private CalendarView mCalendarView;
      private FrameLayout  mRoot;
      private MonthPage    mMonth;

      private int mCount = 1;

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );

            //initView();
      }

      private void testMonth00 ( ) {

            Date date = CalendarUtils.updateDayOfMonth( new Date(), 13 );
            mMonth.setInfo( true, date, 0 );
            mMonth.post( new Runnable() {

                  @Override
                  public void run ( ) {

                        mMonth.folded();
                  }
            } );

            mTitle.setOnClickListener( new OnClickListener() {

                  @Override
                  public void onClick ( View v ) {

                        Log.i( TAG, "onClick: " );
                        mMonth.moving( 100 * mCount );

//                        if( mCount % 2 == 0 ) {
//                              mMonth.expanded();
//                        } else {
//                              mMonth.folded();
//                        }
                        mCount++;
                  }
            } );
      }

      private void initView ( ) {

//            mRoot = findViewById( R.id.root );
//            mTitle = findViewById( R.id.title );
//            mCalendarView = findViewById( R.id.calendarView );
//            mCalendarView.setFirstDayMonday( true );
//
//            final PagerMonthLayout monthLayout = (PagerMonthLayout) mCalendarView.getMonthLayout();
//            monthLayout.post( new Runnable() {
//
//                  @Override
//                  public void run ( ) {
//
//                        int currentItem = monthLayout.getCurrentItem();
//                        Date date = monthLayout.getDate( currentItem );
//                        String yearMonthFormat = CalendarUtils.getYearMonthFormat( date );
//                        mTitle.setText( yearMonthFormat );
//                  }
//            } );
//
//            monthLayout.addOnPageChangeListener( new SimpleOnPageChangeListener() {
//
//                  @Override
//                  public void onPageSelected ( int position ) {
//
//                        super.onPageSelected( position );
//                        Date date = monthLayout.getDate( position );
//                        String format = CalendarUtils.getYearMonthFormat( date );
//                        mTitle.setText( format );
//                  }
//            } );

            mTitle = (TextView) findViewById( R.id.title );
            mMonth = (MonthPage) findViewById( R.id.month );
      }
}
