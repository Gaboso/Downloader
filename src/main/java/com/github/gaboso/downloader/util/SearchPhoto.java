package com.github.gaboso.downloader.util;

import com.github.gaboso.downloader.text.Textual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.Properties;

public class SearchPhoto {

    private static final Logger LOGGER = LogManager.getLogger(SearchPhoto.class);

    private static final String PNG_JPG_GIF_PATTERN = "img[src~=\\.(png|jpe?g|gif)][class!=pure-img]";
    private static String[] adsList = { };

    static {
        try {
            Properties prop = new Properties();
            prop.load(SearchPhoto.class.getResourceAsStream("/config.properties"));

            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                if ("ads".equals(entry.getKey())) {
                    String values = (String) entry.getValue();
                    adsList = values.split(",");
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String host = "";
    private String destinationFolder = "";
    private String urlText = "";

    public Document getPage(String url, String destinationName) {
        destinationFolder = destinationName;
        host = NetUtils.getHostUrl(url);

        Document document = null;

        try {
            LOGGER.info("Opening connection with: {}", url);
            Connection connection = url.contains(Textual.HTTPS)
                ? SSLHelper.getConnection(url)
                : Jsoup.connect(url);

            document = connection.timeout(8000).get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return document;
    }

    public Elements getImageElements(Document document) {
        return document.select(PNG_JPG_GIF_PATTERN);
    }

    public void dowloadAllImages(Elements elements, String attrName) {
        for (Element element : elements) {
            urlText = element.attr(attrName);
            String name = getFileNameFromUrl();
            downloadImage(name, urlText);
        }
    }

    private String getFileNameFromUrl() {
        String[] parts = urlText.split("/");
        int last = parts.length - 1;
        String fileName = parts[last];

        updateUrlWhenIsRelative();

        if (fileName.contains("-")) {
            int dashIndex = fileName.lastIndexOf('-');
            int dotIndex = fileName.lastIndexOf('.');

            String resolution = fileName.substring(dashIndex + 1, dotIndex).toLowerCase();

            if (resolution.matches("\\d{2,4}x\\d{2,4}")) {
                String start = fileName.substring(0, dashIndex);
                String end = fileName.substring(dotIndex);
                String newFileName = start + end;
                urlText = urlText.replace(fileName, newFileName);
                fileName = newFileName;
            }
        }

        return NetUtils.removeURLParams(fileName);
    }

    public void downloadImage(String name, String urlText) {
        String filePath = generateFilePath(name);
        File file = new File(filePath);

        final boolean isAdsImage = isAdsImage(urlText);
        final boolean fileExists = file.exists();

        if (!isAdsImage && !fileExists) {
            try (
                ReadableByteChannel readChannel = Channels.newChannel(new URL(urlText).openStream());
                FileOutputStream fileOS = new FileOutputStream(filePath)
            ) {

                FileChannel writeChannel = fileOS.getChannel();
                writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
                LOGGER.info("File saved with name: {}", name);

            } catch (IOException e) {
                LOGGER.error("Error while downloading image from url: {} ", urlText, e);
            }
        }
        if (isAdsImage) {
            LOGGER.info("Ads found in: {}", urlText);
        }

        if (fileExists) {
            LOGGER.info("File: {} already exists!", name);
        }
    }

    private void updateUrlWhenIsRelative() {
        if (!urlText.startsWith(Textual.HTTP)) {
            String end = urlText;

            if (host.endsWith("/") && end.startsWith("/")) {
                host = host.substring(0, host.length() - 1);
            }

            urlText = host.endsWith("/") || end.startsWith("/") ?
                host + end :
                host + "/" + end;
        }
    }

    private String generateFilePath(String name) {
        String filePath;

        if (destinationFolder.endsWith(File.separator)) {
            filePath = destinationFolder + name;
        } else {
            filePath = destinationFolder + File.separator + name;
            destinationFolder = destinationFolder + File.separator;
        }

        return filePath;
    }

    private boolean isAdsImage(String url) {
        String urlLowered = url.toLowerCase();

        for (String ads : adsList) {
            if (urlLowered.contains(ads)) {
                return true;
            }
        }

        return false;
    }

}
