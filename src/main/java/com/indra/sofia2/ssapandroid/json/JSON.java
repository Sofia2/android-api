package com.indra.sofia2.ssapandroid.json;

/**
 * Created by mbriceno on 03/01/2018.
 */


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.indra.sofia2.ssapandroid.ssap.SSAPVersion;

public class JSON {

    public static String serialize(Object o) throws IOException {

        return getObjectMapper().writeValueAsString(o);
    }

    public static String serialize(Object o, SSAPVersion version) throws IOException {

        return getObjectMapper(version).writeValueAsString(o);
    }

    public static <T> String serializeCollection(Collection<T> c) throws IOException {
        return getObjectMapper().writeValueAsString(c);
    }

    public static <T> T deserialize(String json, Class<T> clazz) throws IOException {

        return getObjectMapper().readValue(json, clazz);
    }

    public static <T> T deserialize(InputStream json, Class<T> clazz) throws IOException {

        return getObjectMapper().readValue(json, clazz);
    }

    public static <T> Collection<T> deserializeCollection(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = getObjectMapper();
        TypeFactory t = TypeFactory.defaultInstance();
        return mapper.readValue(json, t.constructCollectionType(ArrayList.class,clazz));
    }

    public static JsonNode deserializeToJson(String json, boolean jsonizeString) throws IOException {

        if(!json.startsWith("[") && !json.endsWith("]") && !json.startsWith("{") && !json.endsWith("}") && json.contains("-"))
            json = "\"" + json + "\"";


        if(jsonizeString)
            return getObjectMapper().readTree(jsonizeString(json));
        else
            return getObjectMapper().readTree(json);
    }

    public static JsonNode deserializeToJsonBis(String json) throws IOException {

        return getObjectMapper().readTree(json);

    }
    private static ObjectMapper getBaseObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

        return mapper;

    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = getBaseObjectMapper();
        SSAPJsonModule m = new SSAPJsonModule();
        mapper.registerModule(m);

        return mapper;
    }

    public static ObjectMapper getObjectMapper(SSAPVersion version) {
        ObjectMapper mapper = getBaseObjectMapper();
        SSAPJsonModule m = new SSAPJsonModule(version);
        mapper.registerModule(m);
        return mapper;
    }

    public static String jsonizeString(String value) {



        StringBuilder sbu = new StringBuilder();
        sbu.append(value
                        .replaceAll("\\\\", "")
                        .replaceAll("\"\\{", "{")
                        .replaceAll("}\"", "}")
                        .replaceAll("\"\\[", "[")
                        .replaceAll("\\]\"", "]")
                //.replaceAll("\\\"\\\"", "\"")
        );

        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("ObjectId\\(\\\"\\w*\\\"\\)");
        Matcher matcher = pattern.matcher(sbu);
        while(matcher.find()){
            String key = matcher.group();
            matcher
                    .appendReplacement(sb,
                            key
                                    .replaceAll("ObjectId\\(", Matcher.quoteReplacement("\\ {\"\\$oid\" : "))
                                    .replaceAll("\\)", "\\} "));
        }
        matcher.appendTail(sb);

        String ret = sb.toString();

        if(!ret.startsWith("{") && !ret.endsWith("}") && ret.contains("-"))
            ret = "\"" + ret + "\"";

        //ret = patchNestedQuotes(ret);
        return ret;
    }
}
