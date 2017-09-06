package com.baifendian.swordfish.server.sparksql.common;

public enum FlowStatus {
  /**
   * 0(初始化) 1(调度依赖任务中) 2(调度依赖资源中) 3(正在运行) 4(运行成功) 5(kill掉) 6(运行失败) 7(依赖失败) 8(暂停)
   **/
  INIT, WAITING_DEP, WAITING_RES, RUNNING, SUCCESS, KILL, FAILED, DEP_FAILED, INACTIVE;
}
