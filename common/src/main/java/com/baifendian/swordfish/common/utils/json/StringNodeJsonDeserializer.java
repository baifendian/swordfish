/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月8日
 * File Name      : StringNodeJsonDeserializer.java
 */

package com.baifendian.swordfish.common.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * 字符串节点的自定义反序列化
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月8日
 */
public class StringNodeJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        return node.toString();
    }

}
