package com.github.gaboso.photo.util;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class NetUtils {

    private static final Logger LOGGER = Logger.getLogger(NetUtils.class);

    private NetUtils() {
    }

    public static String getHostUrl(String urlText) {
        try {
            URL url = new URL(urlText);
            String hostText = url.getHost();
            String protocol = url.getProtocol();

            return protocol + "://" + hostText + "/";
        } catch (MalformedURLException e) {
            LOGGER.error("Erro durante acesso a url: " + urlText, e);
        }
        return urlText;
    }

    public static String removeURLParams(String urlText) {
        return urlText.contains("?") ?
                urlText.substring(0, urlText.indexOf('?')) :
                urlText;
    }

}
