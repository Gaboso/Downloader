package br.com.gaboso.photo.util;

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
    public static void connection(String url, String destinationName) {
        destinationFolder = destinationName;
        host = NetUtils.getHostUrl(url);

        try {
            LOGGER.info("Abrindo conexão com: " + url);
            Document document = Jsoup.connect(url).timeout(3000).get();
            LOGGER.info("Conexão estabelecida com: " + url);
            Elements elements = document.select(PNG_JPG_GIF_PATTERN);
            scrollElements(elements);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Método para percorrer os elementos e depois chamar o método que cria a imagem
     * a partir dos elementos analisados
     *
     * @param elements - elementos que serão percorridos (Tags com os links das imagens)
     */
    private static void scrollElements(Elements elements) {
        for (Element element : elements) {
            urlText = element.attr("src");
            String name = getFileNameFromUrl();
            String extension = DocUtils.getFileExtension(name);
            createImage(name, extension);
        }
    }

    /**
     * Método que gera uma imagem
     *
     * @param name      - nome da imagem a ser gerada
     * @param extension - extensão da imagem a ser gerada
     */
    private static void createImage(String name, String extension) {
        try {
            if (!isAdsImage()) {
                LOGGER.info("Acessando url: " + urlText);
                URL url = new URL(urlText);

                LOGGER.info("Acessando arquivo na web");
                BufferedImage img = ImageIO.read(url);
                LOGGER.info("Iniciando gravação do arquivo.");

                String filePath = generateFilePath(name);
                DocUtils.write(img, filePath, name, extension);
            } else {
                LOGGER.info("Propaganda descartada: " + urlText);
            }
        } catch (IOException e) {
            LOGGER.error("Erro durante acesso da imagem na url: " + urlText, e);
        }
    }

    private static String generateFilePath(String name) {
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
    private static boolean isAdsImage() {
        String url = urlText.toLowerCase();

        for (String ads : adsList) {
            if (url.contains(ads))
                return true;
        }

        return false;
    }

    /**
     * Metodo para pegar nome do arquivo a partir da url
     *
     * @return String com o nome do arquivo
     */
    private static String getFileNameFromUrl() {
        String[] parts = urlText.split("/");
        int last = parts.length - 1;
        String fileName = parts[last];

        // Se é um caminho relativo
        updateUrlWhenIsRelative();

        //tirar limitação de resolução
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

        //remover parâmetros da url
        fileName = NetUtils.removeURLParams(fileName);

        return fileName;
    }

    private static void updateUrlWhenIsRelative() {
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