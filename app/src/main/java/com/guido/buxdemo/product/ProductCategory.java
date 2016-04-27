package com.guido.buxdemo.product;

import com.guido.buxdemo.util.StringUtil;

/**
 * Created by GRS on 9/19/15.
 */
public enum ProductCategory
{
  STOCKS("stocks"), INDICES("indices"), COMMODITIES("commodities"), FOREX("forex");

  private final String m_name;
  private ProductCategory(String name)
  {
    m_name = name;
  }

  private String getName()
  {
    return m_name;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  protected static ProductCategory stringToCategory(final String category)
  {
    if(StringUtil.isEmpty(category))
    {
      //Bad call; almost inposible, but hey! just check it
      return STOCKS;
    }

    if(StringUtil.isEqual(category, STOCKS.getName()))
    {
      return STOCKS;
    }
    if(StringUtil.isEqual(category,INDICES.getName()))
    {
      return INDICES;
    }
    if(StringUtil.isEqual(category,COMMODITIES.getName()))
    {
      return COMMODITIES;
    }
    if(StringUtil.isEqual(category,FOREX.getName()))
    {
      return FOREX;
    }

    // the show MUST go on, return stocks by default
    return STOCKS;
  }
}
