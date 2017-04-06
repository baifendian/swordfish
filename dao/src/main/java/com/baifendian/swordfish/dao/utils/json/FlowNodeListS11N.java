package com.baifendian.swordfish.dao.utils.json;

import com.baifendian.swordfish.dao.model.FlowNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * Created by caojingwei on 2017/4/6.
 */
public class FlowNodeListS11N extends JsonSerializer<Object> {

  @Override
  public void serialize(Object object, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    List<FlowNode> flowNodes = (List<FlowNode>) object;
    jgen.writeStartArray();
    for (FlowNode flowNode:flowNodes){
      jgen.writeStartObject();
      //jgen.writeObjectField("model", flowNode);
      jgen.writeString(JsonUtil.toJsonString(flowNode));
      jgen.writeEndObject();
    }
    jgen.writeEndArray();
  }
}
