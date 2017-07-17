package com.baifendian.swordfish.common.job.utils.node.storm;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by caojingwei on 2017/7/15.
 */
public class StormRestUtilTest {

  @Test
  public void testGetTopologySummary(){
    try {
      String res = StormRestUtil.getTopologySummary();
      System.out.println(res);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
