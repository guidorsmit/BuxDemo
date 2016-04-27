package com.guido.buxdemo.product;

import com.guido.buxdemo.util.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representaion of the opening hours of a specific day
 */
public class ProductOpeningHours
{
  private Date m_open;
  private Date m_close;

  protected ProductOpeningHours(String open,String close)
  {
    m_open = DateUtil.parse(open,DateUtil.HH_MM);
    m_close = DateUtil.parse(close,DateUtil.HH_MM);
  }

  public Date getOpeningTime(){return m_open;}
  public Date getClosingTime(){return m_close;}
}
