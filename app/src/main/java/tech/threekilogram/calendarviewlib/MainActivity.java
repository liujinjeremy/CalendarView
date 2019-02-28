package tech.threekilogram.calendarviewlib;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Date;
import tech.threekilogram.calendarview.CalendarUtils;
import tech.threekilogram.calendarview.CalendarView;
import tech.threekilogram.calendarview.CalendarView.OnDateChangeListener;

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

            mCalendar.getMonthLayout().setBackgroundColor( Color.LTGRAY );
            mCalendar.setOnDateChangeListener( new OnDateChangeListener() {

                  @Override
                  public void onNewPageSelected ( Date date ) {

                        mTitle.setText( CalendarUtils.getDateFormat( date ) );
                  }

                  @Override
                  public void onNewDateSelected ( Date newDate ) {

                        mTitle.setText( CalendarUtils.getDateFormat( newDate ) );
                  }
            } );
            mCalendar.setFirstDayMonday( false );
            Calendar calendar = Calendar.getInstance();
            calendar.set( 1990, 7, 26 );
            mCalendar.setDate( calendar.getTime() );
            mCalendar.setMonthMode( false );

            mTitle.setText( CalendarUtils.getDateFormat( calendar.getTime() ) );
      }
}
