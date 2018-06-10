package com.github.gaboso.photo.util;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class SearchPhoto {

    private static final Logger LOGGER = Logger.getLogger(SearchPhoto.class);

    private static final String PNG_JPG_GIF_PATTERN = "img[src~=\\.(png|jpe?g|gif)][class!=pure-img]";

    private static String host = "";
    private static String destinationFolder = "";
    private static String urlText = "";

    private static String[] adsList = {};

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

    /**
     * Metodo que estabelece a conexão com a url passada
     *
     * @param url             - Url qual deve ser acessada
     * @param destinationName - Local onde deve ser salvo as imagens recuperadas a partir url
     */
    public Document getPage(String url, String destinationName) {
        destinationFolder = destinationName;
        host = NetUtils.getHostUrl(url);

        Document document = null;

        try {
            LOGGER.info("Abrindo conexão com: " + url);
            document = Jsoup.connect(url).timeout(3000).get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return document;
    }

    public Elements getImageElements(Document document) {
        return document.select(PNG_JPG_GIF_PATTERN);
    }

    public Elements getTextElements(Document document) {
        return document.select("p[class=phrase-item] a span");
    }

    /**
     * Método para percorrer os elementos e depois chamar o método que cria a imagem
     * a partir dos elementos analisados
     *
     * @param elements - elementos que serão percorridos (Tags com os links das imagens)
     */
    public void dowloadAllImages(Elements elements) {
        for (Element element : elements) {
            urlText = element.attr("src");
            String name = getFileNameFromUrl();
            String extension = DocUtils.getFileExtension(name);
            downloadImage(name, extension, urlText);
        }
    }

    /**
     * Método que gera uma imagem
     *
     * @param name      - nome da imagem a ser gerada
     * @param extension - extensão da imagem a ser gerada
     */
    public void downloadImage(String name, String extension, String urlText) {
        try {
            if (!isAdsImage(urlText)) {
                LOGGER.info("Browsing url: " + urlText);
                URL url = new URL(urlText);

                LOGGER.info("Downloading file ...");
                BufferedImage img = ImageIO.read(url);
                LOGGER.info("Saving file ...");

                String filePath = generateFilePath(name);
                DocUtils.write(img, filePath, name, extension);
                LOGGER.info("File saved with name: " + name + "." + extension);
            } else {
                LOGGER.info("Ads found in: " + urlText);
            }
        } catch (IOException e) {
            LOGGER.error("Error while downloading image from url: " + urlText, e);
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

    /**
     * Método para verificar se a imagem atual é propaganda
     *
     * @return retorna true para propaganda e false caso contrario
     */
    private boolean isAdsImage(String url) {
        String urlLowered = url.toLowerCase();

        for (String ads : adsList) {
            if (urlLowered.contains(ads))
                return true;
        }

        return false;
    }

    /**
     * Metodo para pegar nome do arquivo a partir da url
     *
     * @return String com o nome do arquivo
     */
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
                String end = fileName.substring(dotIndex, fileName.length());
                String newFileName = start + end;
                urlText = urlText.replace(fileName, newFileName);
                fileName = newFileName;
            }
        }

        return NetUtils.removeURLParams(fileName);
    }

    private void updateUrlWhenIsRelative() {
        if (!urlText.startsWith("http")) {
            String end = urlText;

            if (host.endsWith("/") && end.startsWith("/"))
                host = host.substring(0, host.length() - 1);

            if (host.endsWith("/") || end.startsWith("/"))
                urlText = host + end;
            else
                urlText = host + "/" + end;
        }
    }

}