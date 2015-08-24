package com.navruz.parser.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

/**
 * Created by Navruz on 22.08.2015.
 */
public class LenientGsonConverter extends GsonConverter {

    public LenientGsonConverter(Gson gson) {
        super(gson);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
//            JsonReader jsonReader = new JsonReader(new InputStreamReader(body.in()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in(), Charset.forName("UTF-8")), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) // Read line by line
                sb.append(line + "\n");

            String resString = sb.toString();
            return resString;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.fromBody(body, type);
    }

}
