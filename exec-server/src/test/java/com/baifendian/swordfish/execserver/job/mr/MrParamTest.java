package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.mysql.model.flow.params.Property;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : liujin
 * @date : 2017-03-07 16:59
 */
public class MrParamTest {

    @Test
    public void jsonSer(){
        MrParam param = new MrParam();
        List<Property> properties = new ArrayList<>();
        properties.add(new Property("aa","11"));
        properties.add(new Property("bb","55"));
        param.setProperties(properties);
        param.setMainJar("hadoop-mapreduce-examples-2.7.3.jar");
        param.setMainClass("org.apache.hadoop.examples.ExampleDriver");
        String[] args = {"3", "5"};
        param.setArgs(Arrays.asList(args));
        String[] jars = {"hadoop.jar"};
        param.setJars(Arrays.asList(args));
        String[] files = {"x.conf"};
        param.setFiles(Arrays.asList(files));
        String jsonStr = JsonUtil.toJsonString(param);
        System.out.println(jsonStr);
        JsonUtil.parseObject(jsonStr, MrParam.class);

    }
}
