package com.guido.buxdemo.product;

import com.guido.buxdemo.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * FactoryClass for generating, creating, updating products
 */
public class ProductFactory
{ //FIXME should be implenting generic factory interface. To be Factory independent

  // Product global keys
  private static final String SYMBOL = "symbol";
  private static final String SECURITY_ID = "securityId";
  private static final String DISPLAYNAME = "displayName";
  private static final String CATEGORY = "category";
  private static final String CURRENTPRICE = "currentPrice";
  private static final String CLOSINPRICE = "closingPrice";
  private static final String CURRENCY = "currency";
  private static final String DECIMALS = "decimals";
  private static final String AMOUNT = "amount";
  private static final String BODY = "body";

  // TIME keys
  private static final String TIME_OPENINGHOURS = "openingHours";
  private static final String TIME_TIMEZONE = "timezone";
  private static final String TIME_WEEKDAYS = "weekDays";
  private static final String TIME_START = "start";
  private static final String TIME_STOP = "end";

  public static List<Product> getPredefinedProductList()
  {
    //BTC/USD (Bitcoin): 28625, EUR/GBP: 26620, Germany 30: 26609, Spain 35: 26610, Apple: 26629, Facebook: 26627
    // Ugly way, should request those products in a other way but for now it is OK, Please only call this function once

    List<Product> pList;
    pList = new ArrayList<Product>();
    pList.add(new Product("Bitcoin",28625));
    pList.add(new Product("EUR/GBP",26620));
    pList.add(new Product("Germany 30",26609));
    pList.add(new Product("Spain 35",26610));
    pList.add(new Product("Apple",26629));
    pList.add(new Product("Facebook",26627));

    return pList;
  }

  public static Product create(String name,int productID)
  {
    // Create product
    if(StringUtil.isEmpty(name))
    {
      return null;
    }

    if(productID < 1)
    {
      return null;
    }

    return new Product(name,productID);
  }

  /**
   * Parse all the info from the JSONObject to the given product
   * @param {@link JSONObject}
   * @param {@link Product}
   * @throws JSONException parcing exception
   */
  public static void updateInfo(JSONObject jsonObject, Product p) throws JSONException
  {
    if(p == null)
    {
      return;
    }

    if(p.getId() != jsonObject.getInt(SECURITY_ID))
    {
      return;
    }

    p.setSymbol(jsonObject.getString(SYMBOL));
    p.setDisplayName(jsonObject.getString(DISPLAYNAME));
    p.setCategory(ProductCategory.stringToCategory(jsonObject.getString(CATEGORY)));

    JSONObject jsonObject1;
    ProductPrice pp;
    jsonObject1 = jsonObject.getJSONObject(CURRENTPRICE);
    pp = new ProductPrice(jsonObject1.getDouble(AMOUNT),jsonObject1.getInt(DECIMALS),Currency.stringToCurrency(jsonObject1.getString(CURRENCY)));
    p.setCurrentPrice(pp);

    jsonObject1 = jsonObject.getJSONObject(CLOSINPRICE);
    pp = new ProductPrice(jsonObject1.getDouble(AMOUNT),jsonObject1.getInt(DECIMALS),Currency.stringToCurrency(jsonObject1.getString(CURRENCY)));
    p.setClosingPrice(pp);

    jsonObject1 = jsonObject.getJSONObject(TIME_OPENINGHOURS);
    String timeZone = jsonObject1.getString(TIME_TIMEZONE);
    p.setTimeZone(timeZone);
    JSONArray array = jsonObject1.getJSONArray(TIME_WEEKDAYS);

    for(int i=0;i<array.length();i++)
    {
      JSONArray array2 = array.getJSONArray(i);
      if(array2.length() <= 0)
      {
        // Strange prob closed or on strike;)
        p.addOpeningHours(new ProductOpeningHours("00:00","00:00"));
        continue;
      }

      JSONObject jsonObject2;
      jsonObject2=array2.getJSONObject(0);

      // Assuming all timigns are in UTC
      p.addOpeningHours(new ProductOpeningHours(jsonObject2.getString(TIME_START),jsonObject2.getString(TIME_STOP)));
    }
  }

  /**
   * Update the current price of the given Product
   * @param {@link JSONObject}
   * @param {@link Product}
   * @throws JSONException parcing exception
   */
  public static void updateCurrentPrice(JSONObject jsonObject, Product p) throws JSONException
  {
    if(p == null)
    {
      return;
    }

    JSONObject jsonObject1;
    jsonObject1 = jsonObject.optJSONObject(BODY);
    if(jsonObject1 == null)
    {
      // failed to get body
      return;
    }
    if(p.getId() != jsonObject1.getInt(SECURITY_ID))
    {
      return;
    }

    double currentPrice = jsonObject1.optDouble(CURRENTPRICE); // Note that this curentprice does not return a object. Instead it is returning a double

    p.getCurrentPrice().setAmount(currentPrice);
  }
}
