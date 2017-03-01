/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月18日
 * File Name      : StringNodeJsonSerializer.java
 */

package com.baifendian.swordfish.common.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 字符串节点的自定义序列化
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月18日
 */
public class StringNodeJsonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeRawValue(value);
    }

}
