package com.guido.buxdemo.product;

import com.guido.buxdemo.util.StringUtil;

/**
 * Product price contains all the info for the price of the product
 */
public class ProductPrice
{
  private double m_amount;
  private int m_decimals;
  private Currency m_currency;

  protected  ProductPrice(double amount,int decimals,Currency currency)
  {
    m_amount = amount;
    m_decimals = decimals;
    m_currency = currency;
  }

  public double getAmount()
  {
    return m_amount;
  }

  public int getDecimals()
  {
    return m_decimals;
  }

  public Currency getCurrency()
  {
    return m_currency;
  }

  /**
   *
   * @return String nice representation of the current value
   */
  public String getNiceString()
  {
    return StringUtil.convertDoubleToString(m_amount,m_decimals);
  }

  protected void setAmount(double amount){m_amount = amount;}


  @Override
  public String toString()
  {
    return getNiceString();
  }

}
