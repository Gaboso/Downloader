package br.com.gaboso.photo;

import br.com.gaboso.photo.util.SearchPhoto;
import br.com.gaboso.textual.Textual;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class ClubP {

    public static final String DOT_COM_PHOTOS = Textual.DOT_COM + "/photos/";
    public static final String SPAN_TITLE = "span[class=entry-title]";
    public static final String DIV_GALLERY = "div[class=entry entry-gallery] > a";
    public static final String PATTERN_TITLE = "Photos \\| Club |Photos \\| ";

    public void downloadClubP(String webSiteName, String destinationFolder) {
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
            //pegando o titulo da galeria
            String subFolder = element.select(SPAN_TITLE).text();
            subFolder = subFolder.replace("!", "");
            subFolder = subFolder.replaceAll(" ", "_");
            String newDestinationFolder = destinationFolder + "\\" + subFolder;
            boolean mkdirs = new File(newDestinationFolder).mkdirs();
            search.connection(Textual.HREF, newDestinationFolder);
        }
    }

}