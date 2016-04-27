package com.guido.buxdemo.webservice.androidasync;

import android.net.Uri;

import com.guido.buxdemo.product.Product;
import com.guido.buxdemo.product.ProductFactory;
import com.guido.buxdemo.product.ProductUpdateCallback;
import com.guido.buxdemo.util.StringUtil;
import com.guido.buxdemo.webservice.WebsocketErrorListener;
import com.guido.buxdemo.webservice.WebsocketIF;
import com.guido.buxdemo.webservice.WebsocketUtil;
import com.koushikdutta.async.AsyncServerSocket;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpHead;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpPut;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.callback.RequestCallback;
import com.koushikdutta.async.http.socketio.SocketIORequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AndroidAsync websocket class
 */
public class AndroidAsyncWebsocket extends AsyncHttpClient.JSONObjectCallback implements WebsocketIF, AsyncHttpClient.WebSocketConnectCallback, WebSocket.StringCallback, DataCallback
{
  private List<WebsocketErrorListener> m_errorListenerList;
  private Set<ProductUpdateCallback> m_productCallbackSet;

  private Product m_product;
  private WebSocket m_socket;

  private boolean m_socketReady; // Socket != null & message connected received

  public AndroidAsyncWebsocket()
  {
    m_productCallbackSet = new HashSet<ProductUpdateCallback>();
    m_errorListenerList = new ArrayList<WebsocketErrorListener>();

    m_socket = null;
    m_socketReady = false;
    // Not all data was consumed by Util.emitAllData suspress
    com.koushikdutta.async.Util.SUPRESS_DEBUG_EXCEPTIONS = true;

    createWebsocket();
  }

  @Override
  public void addWebsocketErrorListener(WebsocketErrorListener weCallback)
  {
    if (weCallback == null)
    {
      return;
    }

    m_errorListenerList.add(weCallback);
  }

  @Override
  public void addProductCallback(ProductUpdateCallback puCallback)
  {
    m_productCallbackSet.add(puCallback);
  }

  @Override
  public void removeProductCallback(ProductUpdateCallback puCallback)
  {
    m_productCallbackSet.remove(puCallback);
    if(m_productCallbackSet.isEmpty())
    {
      //Should I do something here no callbacks anymore
    }
  }

  public void getProductInfo(Product p)
  {
    // Request product info from the specified product
    AsyncHttpRequest get;
    int productID;

    if (p == null)
    {
      return;
    }

    m_product = p;
    productID = p.getId();

    // FIXME Strings should be stored in xml or something
    String uri = "https://api.dev.getbux.com/core/8/products/" + productID;
    get = new AsyncHttpGet(uri);

    get.addHeader("Authorization", "Bearer " +
        "eyJhbGciOiJIUzI1NiJ9.eyJzY3AiOlsiYXBwOmxvZ2luIiwicnRmOmxvZ2luIl0sImV4cCI6MTQ1NDA4ODMxM" +
        "Cwic3ViIjoiMzZiN2RiZmEtOGFjYS00MWZkLWE0OGEtNzE1ZGZlZTdiYzgxIiwibmJmIjoxNDIyNTUyMzEwLCJ" +
        "hdWQiOlsiZGV2LmdldGJ1eC5jb20iXSwianRpIjoiMzA5ZWRkMzYtNjFhYi00M2U2LThkYmYtNDZmNDI0NDYyM" +
        "DFjIiwiaWF0IjoxNDIyNTUyMzEwLCJjaWQiOiI4NDczNjIyOTMzIn0.DASNxnda3uScOJaZ7AA_E7gq5P1zz37" +
        "Gc4rCA7ubyU4");
    get.addHeader("Accept", "application/json");
    get.addHeader("Accept-Language", "nl-NL,en;q=0.8");

    get.setLogging("GRS-info", 0);
    AsyncHttpClient.getDefaultInstance().executeJSONObject(get, this);
  }

  @Override
  public void unsubsribeProdcutUpdate(Product p)
  {
    // UNsubscribe to live data feed of the specified product
    JSONObject jsonObject;

    if (p == null)
    {
      return;
    }

    jsonObject = null;
    try
    {
      jsonObject = WebsocketUtil.getUnSubscribeObject(p);
    }
    catch(JSONException e)
    {
      e.printStackTrace();
    }

    if(jsonObject == null)
    {
      return;
    }

    if(m_socketReady)
    {
      m_socket.send(jsonObject.toString());
    }
  }

  @Override
  public void subscribeProductUpdate(Product p)
  {
    // Subscribe to live data feed of the specified product
    JSONObject jsonObject;

    if (p == null)
    {
      return;
    }

    jsonObject = null;
    try
    {
      jsonObject = WebsocketUtil.getSubscribeObject(p);
    }
    catch(JSONException e)
    {
      e.printStackTrace();
    }

    if(jsonObject == null)
    {
      return;
    }

    if(m_socketReady)
    {
      m_socket.send(jsonObject.toString());
    }
  }

  private void createWebsocket()
  {
    // Ceate websocket for subscriptions
    AsyncHttpRequest get;

    String uri = "https://rtf.dev.getbux.com/subscriptions/me";
    get = new AsyncHttpGet(uri);

    // FIXME header info should be static also used in product info
    get.addHeader("Authorization", "Bearer " +
        "eyJhbGciOiJIUzI1NiJ9.eyJzY3AiOlsiYXBwOmxvZ2luIiwicnRmOmxvZ2luIl0sImV4cCI6MTQ1NDA4ODMxM" +
        "Cwic3ViIjoiMzZiN2RiZmEtOGFjYS00MWZkLWE0OGEtNzE1ZGZlZTdiYzgxIiwibmJmIjoxNDIyNTUyMzEwLCJ" +
        "hdWQiOlsiZGV2LmdldGJ1eC5jb20iXSwianRpIjoiMzA5ZWRkMzYtNjFhYi00M2U2LThkYmYtNDZmNDI0NDYyM" +
        "DFjIiwiaWF0IjoxNDIyNTUyMzEwLCJjaWQiOiI4NDczNjIyOTMzIn0.DASNxnda3uScOJaZ7AA_E7gq5P1zz37" +
        "Gc4rCA7ubyU4");
    get.addHeader("Accept-Language", "nl");

    // Debug logging
    //get.setLogging("GRS-subscription", 0);
    AsyncHttpClient.getDefaultInstance().websocket(get, null, this);
  }

  @Override
  public void onCompleted(Exception e, WebSocket socket)
  {
    // Called by AndroidAsync after connect
    if(e != null)
    {
      String errorConnecting="Error connecting subscription ";
      for(WebsocketErrorListener weListener : m_errorListenerList)
      {
        weListener.onError(errorConnecting + e.getMessage());
      }

      return;
    }

    // Set callback for incomming data
    if(socket != null)
    {
      //Assign websocket
      socket.setStringCallback(this);
      socket.setDataCallback(this);
      m_socket = socket;
    }
  }

  @Override
  public void onCompleted(Exception e, AsyncHttpResponse response, JSONObject result)
  {
    // Get complete Product already parsed into JSON. Complete product
    if (e != null)
    {
      String errorReceviingProduct="Error during initial connect " + e.getMessage();
      for(WebsocketErrorListener weListener : m_errorListenerList)
      {
        weListener.onError(errorReceviingProduct);
      }
      return;
    }
    //Product data available. set to product
    updateProduct(result);
  }

  @Override
  public void onStringAvailable(String data)
  {
    // Data received from websocket -> subscription socket
    System.out.println("read "+ data);
    Exception ex;
    ex=null;

    try
    {
      // Sort out what kind of message this is
      if(WebsocketUtil.isConnectionMessage(data))
      {
        // Validate if we are connected
        if(WebsocketUtil.isConnectionMessageError(data))
        {
          ex = new Exception(WebsocketUtil.getWebserviceConnectionError(data));
        }
        else
        {
          if(m_socket != null)
          {
            // We can now make use of the socket
            m_socketReady = true;
          }
        }
      }
      else if(WebsocketUtil.isTradingQuoteMessage(data) )
      {
        // object of trading quote..update current price
        updateProduct(data);
      }
      else
      {
        // Just debug info
        System.out.println("Non important message received\n  " + data );
      }
    }
    catch(Exception e)
    {
      ex= e;
    }

    if(ex != null)
    {
      // Always perform exception check
      String errorDuringLiveUpdate = "Error during live update " +ex.getMessage();
      for (WebsocketErrorListener weListener : m_errorListenerList)
      {
        weListener.onError(errorDuringLiveUpdate);
      }
    }
  }

  @Override
  public void onDataAvailable(DataEmitter emmiter,ByteBufferList bbList)
  {
    // Didnt see this function called. For now it was alway onStringAvailable.. keeping this method to be sure no data is missed
    String data;
    if(bbList!=null)
    {
      data = new String(bbList.getAllByteArray());
      if(StringUtil.isEmpty(data))
      {
        return;
      }
      System.out.println("read data: "+data);
      updateProduct(data);
    }
  }

  /**
   * Call this funtion whenever some {@link Product} data is received.<br/>
   * Data will be sorted out in this function
   * @param {@link JSONObject} representation of prouduct of trading-quote
   * @throws {@link JSONException} parse exception
   * @see -updateProduct(JSONObject data)
   */
  private void updateProduct(String data)
  {
    try
    {
      updateProduct(new JSONObject(data));
    }
    catch (Exception e)
    {
      String errorUpdatingProduct = "Error while parsing data " + e.getMessage();
      for (WebsocketErrorListener weListener : m_errorListenerList)
      {
        weListener.onError(errorUpdatingProduct);
      }
    }
  }

  /**
   * Call this funtion whenever some {@link Product} data is received.<br/>
   * Data will be sorted out in this function
   * @param {@link JSONObject} representation of prouduct of trading-quote
   * @throws {@link JSONException} parse exception
   * @see -updateProduct(String data)
   */
  private void updateProduct(JSONObject jsonObject)
  {
    try
    {
      if (WebsocketUtil.isUpdateOfCurrentPrice(jsonObject))
      {
        ProductFactory.updateCurrentPrice(jsonObject,m_product);
        for (ProductUpdateCallback puCallback : m_productCallbackSet)
        {
          puCallback.onUpdateCurrentPrice(m_product);
        }
      } else
      {// This object contains a full product description
        ProductFactory.updateInfo(jsonObject,m_product);
        for (ProductUpdateCallback puCallback : m_productCallbackSet)
        {
          puCallback.onCompleted(m_product);
        }
      }
    } catch (Exception e)
    {
      String errorUpdatingProduct = "Error while parsing data " + e.getMessage();
      for (WebsocketErrorListener weListener : m_errorListenerList)
      {
        weListener.onError(errorUpdatingProduct);
      }
    }
  }

  @Override
  public void pause()
  {
    if(m_socket != null)
    {
      m_socket.pause();
    }
  }

  @Override
  public void resume()
  {
    if(m_socket != null)
    {
      m_socket.resume();
    }
  }

  @Override
  public void close()
  {
    if(m_socket != null)
    {
      m_socket.close();
    }
  }

}

