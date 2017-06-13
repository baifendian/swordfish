package com.baifendian.swordfish.dao.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by caojingwei on 2017/6/13.
 */
public class JSONArraySerializer extends JsonSerializer<JSONArray> {

  @Override
  public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    String v = value;
    jgen.writeRawValue(v);
  }
}
