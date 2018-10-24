package com.tfd.base.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author TangFD@HF
 */
public class CookieUtils {
    private static final int MINUTE_SECONDS = 60;

    public static void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, "/", null);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, String path) {
        setCookie(response, name, value, path, null);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, int age) {
        setCookie(response, name, value, "/", age + "");
    }

    public static void setCookie(HttpServletResponse response, String name, String value, String path, String age) {
        setCookie(response, name, value, path, age, null);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, String path, String age, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(false);
        cookie.setPath(path);
        if (StringUtils.isNotEmpty(domain)) {
            cookie.setDomain(domain);
        }

        int ageValue = getAgeIntValue(age);
        if (ageValue >= 0) {
            cookie.setMaxAge(ageValue);
        }

        response.addCookie(cookie);
    }

    private static int getAgeIntValue(String age) {
        try {
            return Integer.valueOf(age) * MINUTE_SECONDS;
        } catch (Exception e) {
            return -1;
        }
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        Cookie returnCookie = null;

        if (cookies == null) {
            return null;
        }

        for (Cookie thisCookie : cookies) {
            if (thisCookie.getName().equals(name)) {
                returnCookie = thisCookie;
                break;
            }
        }

        return returnCookie;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        if (cookie == null) {
            return null;
        }

        return cookie.getValue();
    }

    public static String getAllCookieNameAndValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        StringBuilder cs = new StringBuilder();
        for (Cookie cookie : cookies) {
            cs.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        }

        return cs.toString();
    }

    public static void deleteCookie(HttpServletResponse response, Cookie cookie, String path) {
        if (cookie == null) {
            return;
        }

        cookie.setMaxAge(0);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, Cookie cookie) {
        deleteCookie(response, cookie, "/");
    }
}
