package tech.threekilogram.calendarviewlib;

import java.util.Calendar;
import java.util.Date;
import tech.threekilogram.calendarview.CalendarUtils;

/**
 * @author Liujin 2019/2/20:17:02:58
 */
public class Test {

      public static void main ( String[] args ) {

            int count = CalendarUtils.monthDayCount( 2019, 8 );
            int dayOfWeek = CalendarUtils.dayOfWeek( 2019, 8, 1 );

            System.out.println( count + " " + dayOfWeek );

            Calendar instance = Calendar.getInstance();
            for( int i = 0; i < 7; i++ ) {
                  instance.set( 2019, 8, i + 1 );
                  Date time = instance.getTime();
                  int i1 = instance.get( Calendar.DAY_OF_WEEK );
                  System.out.println( time + " " + i1 );
            }
      }
}
