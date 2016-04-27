package com.guido.buxdemo.product;

import com.guido.buxdemo.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Product.. what else do I need to say more..
 */
public class Product
{
  private String m_name;
  private int m_productID;

  private String m_displayName;
  private String m_symbol;
  private int m_securityID;
  private ProductCategory m_category;
  private ProductPrice m_closingPrice;
  private ProductPrice m_currentPrice; // Current price stays null if market is closed

  // Opening
  private List<ProductOpeningHours> m_pohList;
  private String m_timeZone;

  protected Product(String name, int productID)
  {
    m_name = name;
    m_productID = productID;

    m_pohList = new ArrayList<ProductOpeningHours>();
  }

  public int getId()
  {
    return m_productID;
  }

  public String getName()
  {
    return m_name;
  }

  protected void setDisplayName(String displayName){ m_displayName = displayName;}
  protected void setCurrentPrice(ProductPrice currentPrice){m_currentPrice = currentPrice;}
  protected void setClosingPrice(ProductPrice closingPrice){m_closingPrice = closingPrice;}
  protected void setCategory(ProductCategory category){m_category = category;}
  protected void setSymbol(String symbol){m_symbol = symbol;}

  public ProductPrice getCurrentPrice(){return m_currentPrice;}
  public ProductPrice getClosingPrice(){return m_closingPrice;}
  public ProductCategory getCategory(){return m_category;}

  public void addOpeningHours(ProductOpeningHours poh){m_pohList.add(poh);}
  public void setTimeZone(String timeZone){m_timeZone = timeZone;}
  public List<ProductOpeningHours> getProductOpeningHoursList(){return m_pohList;}

  /**
   * Get the price difference between now and closing value of yesterday
   * @return difference in percentage
   */
  public double getDelta()
  {
    if(getCurrentPrice() == null || getClosingPrice() == null )
    {
      return -1; // Either closed or not set yet
    }

    // Calculate the difference in percentage %
    // formula (NEW / OLD) * 100.0;
    return (getCurrentPrice().getAmount() / getClosingPrice().getAmount()) * 100.0d;
  }

  @Override
  public String toString()
  {
    // Create nice representation of the product
    if(StringUtil.isEmpty(m_displayName))
    {
      return m_name + ":" + m_productID;
    }

    return m_displayName;
  }
}
