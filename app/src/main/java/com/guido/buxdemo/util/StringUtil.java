package com.guido.buxdemo.util;

import android.annotation.SuppressLint;

/**
 * StringUtil class to make life a bit more easy
 */
public class StringUtil
{

  /**
   * Compare left string to right string
   * 
   * @param {@Link String} s1
   * @param {@Link String} s2
   * @return
   */
  public static boolean isEqual(String s1, String s2)
  {
    if (s1 == null && s2 == null)
    {
      return true;
    }

    if (s1.isEmpty() && s2.isEmpty())
    {
      return true;
    }

    if (s1.equals(s2))
    {
      return true;
    }

    return false;
  }

  public static String getEmptyString()
  {
    return new String("");
  }

  public static boolean contains(String s1, String contains)
  {
    if (s1 == null && contains == null)
    {
      return true;
    }

    if (s1.isEmpty() && contains.isEmpty())
    {
      return true;
    }

    if (s1.contains(contains))
    {
      return true;
    }

    return false;
  }

  public static boolean isEmpty(String s)
  {
    if (s == null || s.isEmpty())
    {
      return true;
    }

    return false;
  }

  public static String convertDoubleToString(double value,int decimals)
  {
    String patern;

    if(decimals < 0)
    {
      decimals=1; //
    }

    patern = "%."+decimals+"f"; // C++ style, still awesome
    return String.format(patern,value);
  }
}
