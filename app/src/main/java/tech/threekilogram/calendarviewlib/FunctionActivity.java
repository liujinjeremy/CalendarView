package tech.threekilogram.calendarviewlib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import tech.threekilogram.calendar.CalendarView;
import tech.threekilogram.calendar.CalendarView.OnDateChangeListener;
import tech.threekilogram.calendar.util.CalendarUtils;

public class FunctionActivity extends AppCompatActivity {

      private static final String TAG = FunctionActivity.class.getSimpleName();

      private CalendarView mCalendar;
      private TextView     mTextView;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, FunctionActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_function );
            initView();
      }

      private void initView ( ) {

            mCalendar = findViewById( R.id.calendar );
            mCalendar.setBackgroundColor( Color.LTGRAY );
            mTextView = findViewById( R.id.textView );
            mTextView.post( new Runnable() {

                  @Override
                  public void run ( ) {

                        mTextView.setText( CalendarUtils.getDateFormat( mCalendar.getCurrentPageDate() ) );
                  }
            } );
            mCalendar.setOnDateChangeListener( new OnDateChangeListener() {

                  @Override
                  public void onNewPageSelected ( Date date ) {

                        mTextView.setText( CalendarUtils.getDateFormat( mCalendar.getCurrentPageDate() ) );
                  }

                  @Override
                  public void onNewDateClick ( Date newDate ) {

                        mTextView.setText( CalendarUtils.getDateFormat( mCalendar.getCurrentPageDate() ) );
                  }

                  @Override
                  public void onNewDateSet ( Date date ) {

                        mTextView.setText( CalendarUtils.getDateFormat( mCalendar.getCurrentPageDate() ) );
                  }
            } );
      }

      public void changeWeekStart ( View view ) {

            mCalendar.setFirstDayMonday( !mCalendar.isFirstDayMonday() );
      }

      public void resetPageDate ( View view ) {

            mCalendar.setDate( CalendarUtils.get( 1999, 7, 26 ) );
      }

      public void changeMode ( View view ) {

            mCalendar.setMonthMode( !mCalendar.isMonthMode() );
      }

      public void getPageDate ( View view ) {

            ( (TextView) view ).setText( CalendarUtils.getDateFormat( mCalendar.getCurrentPageDate() ) );
      }

      public void open ( View view ) {

            mCalendar.expandToMonthMode();
      }

      public void close ( View view ) {

            mCalendar.foldToWeekMode();
      }

      public void fold100 ( View view ) {

            // mCalendar.getMonthLayout().getCurrentPage().expandFoldBy( -100 );
      }

      public void expand100 ( View view ) {

            //mCalendar.getMonthLayout().getCurrentPage().expandFoldBy( 100 );
      }
}
