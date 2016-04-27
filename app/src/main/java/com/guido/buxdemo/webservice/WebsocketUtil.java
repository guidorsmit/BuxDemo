package com.guido.buxdemo.webservice;

import com.guido.buxdemo.product.Product;
import com.guido.buxdemo.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class for the WebSocket implementation<<br/>
 * To make life a bit more easy
 */
public class WebsocketUtil
{
  private static final String ERROR_MESSAGE = "message";
  private static final String ERROR_DEVELOPER_MESSAGE = "developerMessage";
  private static final String ERROR_CODE = "errorCode";

  private static final String T = "t";
  private static final String T_CONN = "connect.";
  private static final String T_CONNECTED = T_CONN+"connected";
  private static final String T_CONN_ERR =T_CONN+"failed";


  private static final String SUBSCRIBE_TO = "subscribeTo";
  private static final String UNSUBSCRIBE_FROM = "unsubscribeFrom";

  private static final String TRADING_PRODUCT = "trading.product.";
  private static final String TRADING_QUOTE = "trading.quote";


  private static final String BODY = "body";

  public static JSONObject getSubscribeObject(Product p) throws JSONException
  {
    // FIXME should be a array of products.. really!!
    // Create subscription JSONObject
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(SUBSCRIBE_TO,new JSONArray().put(TRADING_PRODUCT + p.getId()));

    return jsonObject;
  }

  public static JSONObject getUnSubscribeObject(Product p) throws JSONException
  {
    // FIXME should be a array of products.. really!!
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(UNSUBSCRIBE_FROM,new JSONArray().put(TRADING_PRODUCT + p.getId()));

    return jsonObject;
  }

  public static boolean isTradingQuoteMessage(String jsonString) throws JSONException
  {
    JSONObject jsonObject;

    jsonObject = new JSONObject(jsonString);
    if(!jsonObject.has(T))
    {
      // Object does not contain any info about the
      return false;
    }

    if (!StringUtil.isEqual(jsonObject.getString(T),TRADING_QUOTE))
    {// Does not contain trading.quote part
      return false;
    }

    return true;
  }

  public static boolean isConnectionMessage(String jsonString) throws JSONException
  {
    JSONObject jsonObject;

    jsonObject = new JSONObject(jsonString);
    if(! jsonObject.has(T))
    {
      // Object does not contain any info about the connection
      return false;
    }

    String jj = jsonObject.getString(T);
    if(!StringUtil.contains(jj, T_CONN))
    { // Does not contain first part of the connection message
      return false;
    }

    return true;
  }

  public static boolean isConnectionMessageError(String jsonString) throws JSONException
  {
    JSONObject jsonObject;

    jsonObject = new JSONObject(jsonString);
    if(! jsonObject.has(T))
    {
      // Object does not contain any info about the
      return false;
    }

    String jj=jsonObject.getString(T);
    if(!StringUtil.isEqual(jj,T_CONN_ERR))
    { // Does not contian error message
      return false;
    }

    return true;
  }

  public static String getWebserviceConnectionError(String jsonString) throws JSONException
  {
    JSONObject jsonObject;
    JSONObject jsonObject1;

    jsonObject = new JSONObject(jsonString);
    jsonObject1 = jsonObject.getJSONObject(BODY);
    if(jsonObject1 == null)
    {
      return "parsing of object failed, hmm really strange";
    }

    return jsonObject1.getString(ERROR_DEVELOPER_MESSAGE) + " " + jsonObject1.getString(ERROR_CODE);
  }

  public static boolean isUpdateOfCurrentPrice(JSONObject jsonObject)
  {
    // The token symbol is not contained in a update of the current price so check it
    return  jsonObject.has(T);
  }

}
