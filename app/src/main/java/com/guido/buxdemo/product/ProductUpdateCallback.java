package com.guido.buxdemo.product;

/**
 * ProductUpdateCallback
 */
public interface ProductUpdateCallback
{
  /**
   * Update the current current-price
   * @param p
   */
  public void onUpdateCurrentPrice(Product p);

  /**
   * Update the complete Product
   * @param {@link Product}
   */
  public void onCompleted(Product p);
}
