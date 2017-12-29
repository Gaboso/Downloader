package com.github.gaboso.photo.module;

import com.github.gaboso.photo.text.Textual;
import com.github.gaboso.photo.util.SearchPhoto;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClubP {

    private static final Logger LOGGER = Logger.getLogger(ClubP.class);

    private static final String DOT_COM_PHOTOS = Textual.DOT_COM + "/photos/";
    private static final String SPAN_TITLE = "span[class=entry-title]";
    private static final String DIV_GALLERY = "div[class=entry entry-gallery] > a";
    private static final String PATTERN_TITLE = "Photos \\| Club |Photos \\| ";
    //To download all
    private static final String LINKS = "td > a";
    private static final String BLOG_LINK = "http://mypornstarblogs.com/";

    public void downloadAll(String destinationFolder) {
        List<String> addressList = new ArrayList<>();

        try {
            Document document = Jsoup.connect(BLOG_LINK).timeout(8000).get();

            //pegando todas as galerias
            Elements elements = document.select(LINKS);

            for (Element element : elements) {
                String textInLink = element.childNode(0).attr("text");
                if (!"Pictures".equals(textInLink) && !"Videos".equals(textInLink)) {
                    String href = element.attr("href");
                    if (href.contains(Textual.HTTP_WWW) && href.contains(Textual.DOT_COM)) {
                        href = href.replace(Textual.HTTP_WWW, "");
                        href = href.replace(Textual.DOT_COM, "");
                        addressList.add(href);
                    }
                }
            }

            ExecutorService exec = Executors.newFixedThreadPool(4);

            for (String item : addressList) {
                exec.submit(() -> download(item, destinationFolder));
            }

            exec.shutdown();

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void download(String webSiteName, String destinationFolder) {
        String url = Textual.HTTP_WWW + webSiteName + DOT_COM_PHOTOS;

        try {
            if (url.length() > 0 && !"".equals(url.trim())) {
                if (destinationFolder.length() > 0 && !"".equals(destinationFolder.trim())) {
                    Document document = Jsoup.connect(url).timeout(3000).get();
                    String title = document.title();
                    String[] titleParts = title.split(PATTERN_TITLE);
                    destinationFolder += titleParts[1];

                    //pegando todas as galerias
                    Elements elements = document.select(DIV_GALLERY);
                    runElements(elements, destinationFolder);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void runElements(Elements elements, String destinationFolder) {

        for (Element element : elements) {
            String subUrl = element.attr("href");

            String subfolderName = makeSubfolderName(element);

            String newDestinationFolder = destinationFolder + "\\" + subfolderName;
            boolean mkdirs = new File(newDestinationFolder).mkdirs();

            LOGGER.info("Pasta Raiz: " + destinationFolder + "\n\tSub pasta: " + subfolderName);
            LOGGER.info("Foi necessario criar a pasta: " + (mkdirs ? "sim" : "não"));
            SearchPhoto.connection(subUrl, newDestinationFolder);
        }
    }

    private String makeSubfolderName(Element element) {
        String subFolder = element.select(SPAN_TITLE).text();
        subFolder = subFolder.replace("!", "");
        subFolder = subFolder.replaceAll(" ", "_");
        return subFolder;
    }

}