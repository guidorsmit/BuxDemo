package com.guido.buxdemo.product;

import com.guido.buxdemo.util.StringUtil;

/**
 * Currency class
 */
public enum Currency
{
  EUR("EUR"),
  USD("USD");

  private final String m_currency;
  private Currency(String currency){m_currency = currency;}

  private String getName()
  {
    return m_currency;
  }


  protected static Currency stringToCurrency(String currency)
  {
    if(StringUtil.isEmpty(currency))
    {
      //Bad call; almost inposible, but hey! just check it
      return EUR;
    }

    if(StringUtil.isEqual(currency, USD.getName()))
    {
      return USD;
    }
    if(StringUtil.isEqual(currency, EUR.getName()))
    {
      return EUR;
    }

    // the show MUST go on, return USD by default
    return USD;
  }

  public String toString()
  {
    return getName();
  }


}
