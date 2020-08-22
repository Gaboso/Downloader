package com.github.gaboso.photo.module;

import com.github.gaboso.photo.text.Textual;
import com.github.gaboso.photo.util.SSLHelper;
import com.github.gaboso.photo.util.SearchPhoto;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClubP {

    private static final Logger LOGGER = Logger.getLogger(ClubP.class);

    private static final String DOT_COM_PHOTOS = Textual.DOT_COM + "/photos/";
    private static final String SPAN_TITLE = "span[class=entry-title]";
    private static final String DIV_GALLERY = "div[class=entry entry-gallery] > a";
    private static final String PATTERN_TITLE = "Photos \\| Club |Photos \\| ";
    //To download all
    private static final String LINKS = "li > p > a";
    private static final String BLOG_LINK = "https://www.mypornstarblogs.com/pornstars/";

    public void downloadAll(String destinationFolder) {
        List<String> addressList = new ArrayList<>();

        try {
            Document document = SSLHelper.getConnection(BLOG_LINK)
                    .timeout(8000)
                    .get();

            //pegando todas as galerias
            Elements elements = document.select(LINKS);

            for (Element element : elements) {
                String textInLink = element.text();
                if (!"Pictures".equals(textInLink) && !"Videos".equals(textInLink)) {
                    String href = element.attr("href");
                    if ((href.contains(Textual.HTTPS) || href.contains(Textual.HTTP)) && href.contains(Textual.DOT_COM)) {
                        href = href
                                .replace(Textual.WWW_DOT, "")
                                .replace(Textual.DOT_COM, "")
                                .replace("/photos/", "")
                                .replace("/videos/", "");

                        if (!addressList.contains(href)) {
                            addressList.add(href);
                        }
                    }
                }
            }

            for (String address : addressList) {
                download(address, destinationFolder);
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void download(String webSiteName, String destinationFolder) {
        String url = webSiteName + DOT_COM_PHOTOS;

        try {
            if (!destinationFolder.isEmpty()) {
                Connection connection = url.contains(Textual.HTTPS)
                        ? SSLHelper.getConnection(url)
                        : Jsoup.connect(url);

                Document document = connection.timeout(6000).get();
                String title = document.title();
                String[] titleParts = title.split(PATTERN_TITLE);
                destinationFolder += File.separator + titleParts[1];

                //pegando todas as galerias
                Elements elements = document.select(DIV_GALLERY);
                runElements(elements, destinationFolder);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void runElements(Elements elements, String destinationFolder) {

        for (Element element : elements) {
            String subUrl = element.attr("href");

            String subfolderName = makeSubfolderName(element);

            String newDestinationFolder = destinationFolder + File.separator + subfolderName;
            boolean mkdirs = new File(newDestinationFolder).mkdirs();

            LOGGER.info("Pasta Raiz: " + destinationFolder + "\r\n\tSub pasta: " + subfolderName);
            LOGGER.info("Foi necessario criar a pasta: " + (mkdirs ? "sim" : "não"));

            SearchPhoto searchPhoto = new SearchPhoto();
            Document page = searchPhoto.getPage(subUrl, newDestinationFolder);
            Elements imageElements = getImageElementsDefault(page);

            if (!imageElements.isEmpty()) {
                searchPhoto.dowloadAllImages(imageElements, "href");
            } else {
                Elements imageElementsSecondary = getImageElementsSecondary(page);
                searchPhoto.dowloadAllImages(imageElementsSecondary, "data-src");
            }
        }
    }

    private Elements getImageElementsDefault(Document page) {
        return page.select("dl > dt > a");
    }

    private Elements getImageElementsSecondary(Document page) {
        return page.select("img[loading][data-src]");
    }

    private String makeSubfolderName(Element element) {
        String subFolder = element.select(SPAN_TITLE).text();
        subFolder = subFolder.replace("!", "")
                .replace(" ", "_");
        return subFolder;
    }

}
