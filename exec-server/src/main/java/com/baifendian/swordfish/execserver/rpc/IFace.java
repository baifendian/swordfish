package com.baifendian.swordfish.execserver.rpc;

import org.apache.thrift.TException;

/**
 * @author : liujin
 * @date : 2017-03-02 18:05
 */
public interface IFace {
    public RetInfo execFlow(int projectId, long execId, String flowType) throws TException;

    public RetInfo scheduleExecFlow(int projectId, long execId, String flowType, long scheduleDate) throws TException;
}
