package com.guido.buxdemo.product;

import com.guido.buxdemo.util.DateUtil;

import java.util.Date;
import java.util.List;

public class ProductUtil
{
  /**
   * Check if the stock market is open
   * @param p
   * @param localDate
   * @return true if the market is open!
   */
  public static boolean isStockOpen(Product p,Date localDate)
  {
    ProductOpeningHours poh;
    List<ProductOpeningHours> pohList;
    Date utcTime;
    int day;

    //Convert localDate to UTC
    utcTime = DateUtil.convertToUTC(localDate);
    day = DateUtil.getDayOfWeek(utcTime);

    pohList = p.getProductOpeningHoursList();
    if(day >= pohList.size())
    {
      // Something went terribly wrong here. We do not have a complete list.
      // Or we are living in a day we dont have a name for;)
      return false;
    }

    poh = pohList.get(day);
    if(poh == null)
    {
      return false;
    }

    if(!DateUtil.isTimeBetweenDate(utcTime,poh.getOpeningTime(),poh.getClosingTime()))
    {
      return false;
    }
    return true;
  }
}
