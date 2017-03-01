package com.baifendian.swordfish.common.utils.json;

import com.baifendian.swordfish.common.consts.Constants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by caojingwei on 16/8/24.
 */
public class DateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        DateFormat formatter = new SimpleDateFormat(Constants.BASE_DATETIME_FORMAT);
        String formattedDate = formatter.format(date);
        jsonGenerator.writeString(formattedDate);
    }
}
