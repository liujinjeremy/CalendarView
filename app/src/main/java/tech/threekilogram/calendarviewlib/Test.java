package tech.threekilogram.calendarviewlib;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Liujin 2019/2/20:17:02:58
 */
public class Test {

      public static void main ( String[] args ) {

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date );

            calendar.set( 2019, 2 - 1, 1 );
            int year = calendar.get( Calendar.YEAR );
            int month = calendar.get( Calendar.MONTH );
            int day = calendar.get( Calendar.DAY_OF_MONTH );
            int week = calendar.get( Calendar.DAY_OF_WEEK );

            String format = String.format( "%d-%d-%d", year, month, day );
            System.out.println( format );

            calendar.set( 2019, 3 - 1, 1 );
            calendar.add( Calendar.DAY_OF_MONTH, -1 );
            int dayLast = calendar.get( Calendar.DAY_OF_MONTH );
            int weekLast = calendar.get( Calendar.DAY_OF_WEEK );

            System.out.println( day + " " + week + " " + dayLast + " " + weekLast );
      }
}
