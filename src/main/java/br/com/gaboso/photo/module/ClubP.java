package br.com.gaboso.photo.module;

import br.com.gaboso.photo.util.SearchPhoto;
import br.com.gaboso.photo.text.Textual;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static br.com.gaboso.photo.text.Textual.DOT_COM;
import static br.com.gaboso.photo.text.Textual.HTTP_WWW;

public class ClubP {

    private static final String DOT_COM_PHOTOS = Textual.DOT_COM + "/photos/";
    private static final String SPAN_TITLE = "span[class=entry-title]";
    private static final String DIV_GALLERY = "div[class=entry entry-gallery] > a";
    private static final String PATTERN_TITLE = "Photos \\| Club |Photos \\| ";
    //To download all
    private static final String LINKS = "td > a";
    private static final String BLOG_LINK = "http://mypornstarblogs.com/";

    public void downloadAll(String destinationFolder){
        List<String> addressList = new ArrayList<>();

        try {
            Document document = Jsoup.connect(BLOG_LINK).timeout(8000).get();

            //pegando todas as galerias
            Elements elements = document.select(LINKS);

            for (Element element : elements) {
                String textInLink = element.childNode(0).attr("text");
                if (!textInLink.equals("Pictures") && !textInLink.equals("Videos")) {
                    String href = element.attr("href");
                    if (href.contains(HTTP_WWW) && href.contains(DOT_COM)) {
                        href = href.replace(HTTP_WWW, "");
                        href = href.replace(DOT_COM, "");
                        addressList.add(href);
                    }
                }
            }

            for (String item : addressList) {
                download(item, "C:\\photos\\");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(String webSiteName, String destinationFolder) {
        String url = Textual.HTTP_WWW + webSiteName + DOT_COM_PHOTOS;

        try {
            if (url.length() > 0 && !url.trim().equals("")) {
                if (destinationFolder.length() > 0 && !destinationFolder.trim().equals("")) {
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
            e.printStackTrace();
        }
    }

    private void runElements(Elements elements, String destinationFolder) {
        SearchPhoto search = new SearchPhoto();

        for (Element element : elements) {
            String subUrl = element.attr("href");
            //pegando o titulo da galeria
            String subFolder = element.select(SPAN_TITLE).text();
            subFolder = subFolder.replace("!", "");
            subFolder = subFolder.replaceAll(" ", "_");
            String newDestinationFolder = destinationFolder + "\\" + subFolder;
            boolean mkdirs = new File(newDestinationFolder).mkdirs();
            search.connection(subUrl, newDestinationFolder);
        }
    }

}