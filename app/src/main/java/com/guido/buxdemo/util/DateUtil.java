package com.guido.buxdemo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper class for date related stuf
 */
public class DateUtil
{
  public static final String YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";
  public static final String DD_MM_HH_MM = "dd/MM HH:mm";
  public static final String HH_MM = "HH:mm";

  public static Date getDateNow()
  {
    return new Date();
  }

  public static Date parse(String dateString,String format)
  {
    DateFormat formatter;

    if (dateString == null)
    {
      return null;
    }

    formatter = new SimpleDateFormat(format);
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    try
    {
      return formatter.parse(dateString);
    }
    catch (Exception ex)
    {
      // do not print Stack Trace
      // string is not valid
      return null;
    }
  }

  public static int getDayOfWeek(Date date)
  {
    if(date == null)
    {
      return Calendar.SUNDAY;
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return  c.get(Calendar.DAY_OF_WEEK);
  }

  public static Date convertToUTC(Date date)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

    // Convert Local Time to UTC
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String sDate= sdf.format(date);
    try
    {
      //return sdf.parse(sDate);
      return new Date(sdf.format(date));
    }
    catch(Exception e)
    {
      //e.printStackTrace();
      return date;
      // No need to print stack-trace
    }
 }

  public static boolean isTimeBetweenDate(Date d1, Date begin, Date end)
  {
    if(d1 == null || begin == null || end == null)
    {
      return false;
    }

    Calendar c;
    Calendar start;
    Calendar close;

    int cSeconds;
    int startSeconds;
    int endSeconds;

    c = Calendar.getInstance();
    c.setTime(d1);
    cSeconds = (c.get(Calendar.HOUR_OF_DAY)*60*60) + (c.get(Calendar.MINUTE)*60) + (c.get(Calendar.SECOND));

    start = Calendar.getInstance();
    start.setTime(begin);
    startSeconds = (start.get(Calendar.HOUR_OF_DAY)*60*60) + (start.get(Calendar.MINUTE)*60) + (start.get(Calendar.SECOND));


    close = Calendar.getInstance();
    close.setTime(end);
    endSeconds = (close.get(Calendar.HOUR_OF_DAY)*60*60) + (close.get(Calendar.MINUTE)*60) + (close.get(Calendar.SECOND));


    if(cSeconds < startSeconds)
    {
      // Not openend yet!
      return false;
    }

    if(cSeconds > endSeconds)
    {
      //Already closed
      return false;
    }

    return true;
  }
}
