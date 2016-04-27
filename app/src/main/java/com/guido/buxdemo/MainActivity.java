package com.guido.buxdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.guido.buxdemo.product.Product;
import com.guido.buxdemo.product.ProductFactory;
import com.guido.buxdemo.product.ProductUpdateCallback;
import com.guido.buxdemo.product.ProductUtil;
import com.guido.buxdemo.util.DateUtil;
import com.guido.buxdemo.util.StringUtil;
import com.guido.buxdemo.webservice.WebsocketErrorListener;
import com.guido.buxdemo.webservice.WebsocketIF;
import com.guido.buxdemo.webservice.androidasync.AndroidAsyncWebsocket;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ProductUpdateCallback, WebsocketErrorListener, AdapterView.OnItemSelectedListener
{
  private static final String EXCHANGE_CLOSED = "market closed";

  // Websocket interface. Behind the interface implemented AndroidAysnc
  // Why AndroidAsync? it was the first :)
  // Why an interface? because it makes the code independent from a certain lib
  private WebsocketIF websocket;
  private Product m_product; // Currently selected product
  private boolean m_subsriptionAllowed; // Check if we need to subscibe or not

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    List<Product> pList;
    ArrayAdapter<Product> dataAdapter;

    TextView tv;
    Spinner spinner;

    spinner = (Spinner) findViewById(R.id.spinner_product);

    pList = new ArrayList<Product>(ProductFactory.getPredefinedProductList());

    dataAdapter = new ArrayAdapter<Product>(this,
        android.R.layout.simple_spinner_item, pList);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinner.setAdapter(dataAdapter);
    spinner.setOnItemSelectedListener(this);

    // Available TextView's labels
    //tv = (TextView) findViewById(R.id.tv_currency);
    //tv = (TextView) findViewById(R.id.tv_actualValue);
    //tv = (TextView) findViewById(R.id.tv_lastValue);
    //tv = (TextView) findViewById(R.id.tv_category);
    //tv = (TextView) findViewById(R.id.tv_delta); //Show either last delta in % or market closed


    // Create WebSocket impl
    websocket = new AndroidAsyncWebsocket(); // Choose for AndroidAsync impl
    websocket.addProductCallback(this);
    websocket.addWebsocketErrorListener(this);

    // Open the websocket request product info
    websocket.getProductInfo(m_product);

    m_subsriptionAllowed = false;
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    if(websocket != null && m_subsriptionAllowed)
    {
      websocket.unsubsribeProdcutUpdate(m_product);
      websocket.close();
    }
  }

  /**
   * Apply synchized to product. product should be read from the get and set methods
   * @return
   */
  private synchronized Product getProduct()
  {
    return m_product;
  }

  private synchronized void setProduct(Product p)
  {
    m_product = p;
  }

  @Override
  public void onResume()
  {
    super.onResume();
    if(websocket != null && m_subsriptionAllowed)
    {
      websocket.resume();
      // Request for subscription since we are active again
      websocket.subscribeProductUpdate(m_product);
    }

  }

  @Override
  public void onPause()
  {
    super.onPause();
    if(websocket != null && m_subsriptionAllowed)
    {
      // Hold.. doing more important stuff than trading probably whatsapp;)
      // Unsubscribe from feed
      websocket.unsubsribeProdcutUpdate(m_product);
      websocket.pause();
    }
  }

  @Override
  public void onUpdateCurrentPrice(final Product p)
  {
    // Update current price ofthe displayed product
    if (p == null ||getProduct().getId() != p.getId())
    {
      // Old product, might be old packet.. please check routing and mask
      return;
    }

    runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        TextView tv;

        // Update delta
        tv = (TextView) findViewById(R.id.tv_delta);
        if (ProductUtil.isStockOpen(p,DateUtil.getDateNow()))
        { // Stock market is open for busniss!
          tv.setText(StringUtil.convertDoubleToString(p.getDelta(), p.getCurrentPrice().getDecimals()) + "%");
        } else
        {
          tv.setText(StringUtil.convertDoubleToString(p.getDelta(), p.getCurrentPrice().getDecimals()) + "% " + EXCHANGE_CLOSED);
          //tv.setText(EXCHANGE_CLOSED);
        }

        // Update actual value
        tv = (TextView) findViewById(R.id.tv_actualValue);
        tv.setText(StringUtil.convertDoubleToString(p.getCurrentPrice().getAmount(), p.getCurrentPrice().getDecimals()));
      }
    });
  }

  @Override
  public void onCompleted(final Product p)
  {
    // Update product info
    if (p == null || p.getId() != getProduct().getId())
    {
      // Not matching probably old product
      return;
    }

    setProduct(p);
    final boolean stockOpen=ProductUtil.isStockOpen(p, DateUtil.getDateNow());
    if(stockOpen)
    {
      //Completed and stock is open -> subscribe
      m_subsriptionAllowed=true;
    }
    else
    {
      m_subsriptionAllowed=false;
    }

        //Update info
        runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            // Update all the labels/TextViews
            TextView tv;

            tv = (TextView) findViewById(R.id.tv_currency);
            tv.setText(p.getCurrentPrice().getCurrency().toString());
            tv = (TextView) findViewById(R.id.tv_actualValue);
            tv.setText(StringUtil.convertDoubleToString(p.getCurrentPrice().getAmount(), p.getCurrentPrice().getDecimals()));
            tv = (TextView) findViewById(R.id.tv_lastValue);
            tv.setText(StringUtil.convertDoubleToString(p.getClosingPrice().getAmount(), p.getClosingPrice().getDecimals()));
            tv = (TextView) findViewById(R.id.tv_category);
            tv.setText(p.getCategory().toString());
            tv = (TextView) findViewById(R.id.tv_delta); //Show either last delta in % or market closed
            if (stockOpen)
            {
              tv.setText(StringUtil.convertDoubleToString(p.getDelta(), p.getCurrentPrice().getDecimals()) + "%");
            } else
            {
              tv.setText(StringUtil.convertDoubleToString(p.getDelta(), p.getCurrentPrice().getDecimals()) + "% " + EXCHANGE_CLOSED);
              //tv.setText(EXCHANGE_CLOSED);
            }
          }
        });

    if(m_subsriptionAllowed)
    { // Register to life feed
      // Subscription only allowed when update was sucseed
      websocket.subscribeProductUpdate(m_product);
    }
  }

  @Override
  public void onError(final String exception)
  {
    // Show the  error message... you should probably turn on youre internet
    runOnUiThread(new Runnable()
    {
      public void run()
      {
        Toast.makeText(getApplicationContext(), exception, Toast.LENGTH_LONG).show();
      }
    });
  }

  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
  {
    Product p;

    p = (Product)parent.getItemAtPosition(pos);
    if(p == null)
    {
      return;
    }

    // Could be null initial
    if(getProduct() != null && getProduct().getId() == p.getId())
    {
      //Same product please do nothing
      return;
    }

    if(getProduct() !=null && m_subsriptionAllowed)
    {
      websocket.unsubsribeProdcutUpdate(getProduct());
    }
    websocket.getProductInfo(p);

    // Assign to global
    setProduct(p);
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0)
  { // Nothing selected.. so please do not anynthing
  }
}
