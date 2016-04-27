package com.guido.buxdemo.webservice;

import com.guido.buxdemo.product.Product;
import com.guido.buxdemo.product.ProductUpdateCallback;

import java.io.File;

/**
 * Interface for Websocket. Keep the code nice and clean by using interfaces!
 */
public interface WebsocketIF
{
  public void addWebsocketErrorListener(WebsocketErrorListener weListener);

  public void addProductCallback(ProductUpdateCallback puCallback);
  public void removeProductCallback(ProductUpdateCallback puCallback);
  public void getProductInfo(Product p);
  public void subscribeProductUpdate(Product p);
  public void unsubsribeProdcutUpdate(Product p);
  public void pause();
  public void resume();
  public void close();
}
