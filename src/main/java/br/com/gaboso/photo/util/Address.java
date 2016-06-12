package br.com.gaboso.photo.util;

import br.com.gaboso.photo.module.ClubP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static br.com.gaboso.photo.text.Textual.DOT_COM;
import static br.com.gaboso.photo.text.Textual.HTTP_WWW;

public class Address {

    private List<String> addressList = new ArrayList<>();

    private static final String LINKS = "td > a";
    private static final String BLOG_LINK = "http://mypornstarblogs.com/";

    public static void main(String[] args) {
        Address address = new Address();

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
                        address.addressList.add(href);
                    }
                }
            }

            ClubP clubP = new ClubP();

            for (String item : address.addressList) {
                clubP.download(item, "C:\\photos\\");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}