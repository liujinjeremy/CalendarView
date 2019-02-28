package tech.threekilogram.calendarviewlib;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import tech.threekilogram.calendarview.CalendarView;
import tech.threekilogram.calendarview.month.MonthLayout;
import tech.threekilogram.calendarview.month.MonthPage;

public class MainActivity extends AppCompatActivity {

      private static final String TAG = MainActivity.class.getSimpleName();

      private int          mCount = 1;
      private TextView     mTitle;
      private CalendarView mCalendar;
      private FrameLayout  mRoot;

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );

            initView();
      }

      private void initView ( ) {

            mTitle = findViewById( R.id.title );
            mCalendar = findViewById( R.id.calendar );
            mRoot = findViewById( R.id.root );

            mTitle.setOnClickListener( new OnClickListener() {

                  @Override
                  public void onClick ( View v ) {

                        MonthLayout child = (MonthLayout) mCalendar.getChildAt( 0 );
                        MonthPage currentPage = child.getCurrentPage();
                        if( currentPage.getState() == 0 ) {
                              currentPage.moving( Integer.MIN_VALUE );
                        }
                        if( currentPage.getState() == 2 ) {
                              currentPage.moving( Integer.MAX_VALUE );
                        }
                  }
            } );
      }
}
