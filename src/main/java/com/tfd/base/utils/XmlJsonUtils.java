package com.tfd.base.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * xml,json 转换工具类
 * <ul>
 * <li>xml转object</li>
 * <li>xml转jsonObject</li>
 * <li>object转xml</li>
 * <li>json转object</li>
 * <li>json转jsonObject</li>
 * <li>object转json</li>
 * <li>xml转Json</li>
 * <li>json转xml</li>
 * </ul>
 *
 * @author TangFD@HF 2018/8/31
 */
public class XmlJsonUtils {

    public static String xml2Json(String xml) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        return xmlSerializer.read(xml).toString();
    }

    public static String json2xml(String json, String rootName) {
        JSONObject object = JSONObject.fromObject(json);
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setSkipNamespaces(true);
        xmlSerializer.setSkipWhitespace(true);
        xmlSerializer.setTypeHintsEnabled(false);
        xmlSerializer.setForceTopLevelObject(false);
        if (StringUtils.isNotEmpty(rootName)) {
            xmlSerializer.setRootName(rootName);
        }

        return xmlSerializer.write(object);
    }

    public static <T> T xml2Object(String xml, Class<T> clazz) {
        String json = xml2Json(xml);
        return HttpUtils.GSON.fromJson(json, clazz);
    }

    public static String object2Xml(Object object, String rootName) {
        XStream xStream = new XStream();
        if (StringUtils.isNotEmpty(rootName)) {
            xStream.alias(rootName, object.getClass());
        }

        return xStream.toXML(object);
    }

    public static JsonObject xml2Object(String xml) {
        String json = xml2Json(xml);
        return getJsonObject(json);
    }

    public static <T> T json2Object(String json, Class<T> clazz) {
        return HttpUtils.GSON.fromJson(json, clazz);
    }

    public static String object2Json(Object object) {
        return HttpUtils.GSON.toJson(object);
    }

    public static JsonObject json2Object(String json) {
        return getJsonObject(json);
    }

    public static <T> T json2Object(InputStream inputStream, Class<T> clazz) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            return HttpUtils.GSON.fromJson(reader, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return null;
    }

    private static JsonObject getJsonObject(String json) {
        JsonElement jsonElement = new JsonParser().parse(json);
        return jsonElement.getAsJsonObject();
    }
}
