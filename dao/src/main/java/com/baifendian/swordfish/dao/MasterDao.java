/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
import com.baifendian.swordfish.dao.mysql.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mysql.model.MasterServer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : liujin
 * @date : 2017-03-10 15:57
 */
public class MasterDao extends BaseDao{

    @Autowired
    MasterServerMapper masterServerMapper;


    @Override
    protected void init() {
        masterServerMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(MasterServerMapper.class);
    }

    public MasterServer getMasterServer(){
        return masterServerMapper.query();
    }

    public registerMasterServer(String host, int port){
        return masterServerMapper.insert()
    }
}
