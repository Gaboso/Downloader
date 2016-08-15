package br.com.gaboso.photo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SearchPhoto {

    private static final String PNG_JPG = "img[src~=\\.(png|jpe?g|gif)][class!=pure-img]";

    private String host = "";
    private String destinationFolder = "";
    private String urlText = "";


    public void connection(String url, String destinationName) {
        destinationFolder = destinationName;
        host = getHostUrl(url);

        try {
            System.out.println("\n\nAbrindo conexão");
            Document document = Jsoup.connect(url).timeout(3000).get();
            System.out.println("Conexão estabelecida.");
            Elements elements = document.select(PNG_JPG);
            scrollElements(elements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para percorrer os elementos e depois chamar o método que cria a imagem
     * a partir dos elementos analisados
     *
     * @param elements - elementos que serão percorridos (Tags com os links das imagens)
     */
    private void scrollElements(Elements elements) {
        for (Element element : elements) {
            urlText = element.attr("src");
            String name = getFileNameFromUrl();
            String extension = getFileExtension(name);
            createImage(name, extension);
        }
    }

    /**
     * Método que gera uma imagem
     *
     * @param name      - nome da imagem a ser gerada
     * @param extension - extensão da imagem a ser gerada
     */
    private void createImage(String name, String extension) {
        try {
            if (!isAdsImage()) {
                System.out.println("\n\nAcessando url: " + urlText);
                URL url = new URL(urlText);
                System.out.println("Acessando arquivo na web");
                BufferedImage img = ImageIO.read(url);
                System.out.println("Iniciando gravação do arquivo.");

                String filePath;
                if (destinationFolder.endsWith("\\"))
                    filePath = destinationFolder + name;
                else {
                    filePath = destinationFolder + "\\" + name;
                    destinationFolder = destinationFolder + "\\";
                }

                File file = new File(filePath);
                if (!file.exists()) {
                    ImageIO.write(img, extension, file);
                    System.out.println("Arquivo: " + name + " gravado!!!");
                } else
                    System.out.println("Arquivo: " + name + " já existente!!!");
            } else
                System.out.println("Propaganda descartada!!! " + urlText + " \n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro durante gravação do arquivo: " + name);
        }
    }

    /**
     * Método para verificar se a imagem atual é propaganda
     *
     * @return retorna true para propaganda e false caso contrario
     */
    private boolean isAdsImage() {
        String url = urlText.toLowerCase();
        String[] adsList = {"/ads/", "brazzers-boobs", "bang-bros-", "/images/smilies/", "naughty-america1",
                "penthouse.jpg", "brazzers-doggy-style.jpg", "banner1.gif", "mofos.gif"};

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
    private String getFileNameFromUrl() {
        String[] parts = urlText.split("/");
        int last = parts.length - 1;
        String fileName = parts[last];

        // Se é um caminho relativo
        if (!urlText.startsWith("http")) {
            String end = urlText;

            if (host.endsWith("/") && end.startsWith("/"))
                host = host.substring(0, host.length() - 1);

            if (host.endsWith("/") || end.startsWith("/"))
                urlText = host + end;
            else
                urlText = host + "/" + end;
        }

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

        //contem parâmetros
        if (fileName.contains("?"))
            fileName = fileName.substring(0, fileName.indexOf("?"));

        return fileName;
    }

    /**
     * Método para pegar extensão do arquivo
     *
     * @param fileName - nome do arquivo
     * @return extensão do arquivo
     */
    private String getFileExtension(String fileName) {
        String[] parts = fileName.split("\\.");
        int last = parts.length - 1;
        return parts[last];
    }

    /**
     * Método para pegar o host da url que foi passada
     *
     * @param urlText - url da qual quer pegar o host
     * @return host pego da url informada
     */
    private String getHostUrl(String urlText) {
        try {
            URL url = new URL(urlText);
            String host = url.getHost();
            String protocol = url.getProtocol();

            return protocol + "://" + host + "/";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlText;
    }

}