package com.tfd.base.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author TangFD@HF 2018/2/28
 */
public class PropertiesUtils {

    private static Properties properties;

    static {
        if (properties == null) {
            properties = new Properties();
            InputStream inStream = PropertiesUtils.class.getClassLoader().getResourceAsStream("config.properties");
            try {
                properties.load(inStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Properties getProperties(String propertyFile) {
        Properties properties = new Properties();
        InputStream inStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(propertyFile);
        try {
            properties.load(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getPropertyValue(String key) {
        return properties.getProperty(key);
    }
}