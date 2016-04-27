package com.guido.buxdemo.webservice;

public interface WebsocketErrorListener
{
  /**
   * OnError, Please parse it to something readable
   * @param {@link String} nice representation of the error
   */
  public void onError(String message);
}
