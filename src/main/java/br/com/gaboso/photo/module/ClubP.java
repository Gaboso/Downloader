package br.com.gaboso.photo.module;

import br.com.gaboso.photo.util.SearchPhoto;
import br.com.gaboso.photo.text.Textual;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class ClubP {

    private static final String DOT_COM_PHOTOS = Textual.DOT_COM + "/photos/";
    private static final String SPAN_TITLE = "span[class=entry-title]";
    private static final String DIV_GALLERY = "div[class=entry entry-gallery] > a";
    private static final String PATTERN_TITLE = "Photos \\| Club |Photos \\| ";

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