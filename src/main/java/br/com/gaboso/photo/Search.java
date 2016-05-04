package br.com.gaboso.photo;

import br.com.gaboso.photo.util.SearchPhoto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class Search {

    public static void main(String[] args) {

        SearchPhoto search = new SearchPhoto();

        try {
            String http = "http://www.";
            String dotComPhotos = ".com/photos/";
            String url = http + "" + dotComPhotos;

            if (url.length() > 0 && !url.trim().equals("")) {

                String destinationFolder = "C:\\photos\\";

                if (destinationFolder.length() > 0 && !destinationFolder.trim().equals("")) {

                    Document document = Jsoup.connect(url).timeout(3000).get();
                    String title = document.title();
                    String[] titleParts = title.split("Photos \\| ");
                    destinationFolder += titleParts[1];

                    Elements elements = document.select("div[class=entry entry-gallery] > a");

                    for (Element element : elements) {
                        String subUrl = element.attr("href");
                        String subFolder = element.select("span[class=entry-title]").text();
                        subFolder = subFolder.replace("!", "");
                        subFolder = subFolder.replaceAll(" ", "_");
                        String newDestinationFolder = destinationFolder + "\\" + subFolder;
                        boolean mkdirs = new File(newDestinationFolder).mkdirs();
                        search.connection(subUrl, newDestinationFolder);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}