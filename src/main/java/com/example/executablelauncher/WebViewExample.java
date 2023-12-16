package com.example.executablelauncher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class WebViewExample extends Application {
    @Override
    public void start(Stage primaryStage) {
        /*WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Cargar una página web
        webEngine.load("https://www.google.com/search?q=attack+on+titan");

        // Esperar hasta que la página se haya cargado completamente
        /*webEngine.documentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Obtener el título de la página
                String pageTitle = (String) webEngine.executeScript("document.getElementsByClassName(\"LatpMc\").href");
                System.out.println("Título de la página: " + pageTitle);
                webEngine.load(pageTitle);

                // Puedes realizar otras operaciones aquí para obtener elementos adicionales
            }
        });

        try {
            Document document = Jsoup.connect("https://www.google.com/search?q=attack+on+titan").get();
            String s = document.getElementsByClass("LatpMc nPDzT T3FoJb").get(0).attr("href");
            //document = Jsoup.connect("https://www.google.com" + s).get();
            //Document doc = Jsoup.connect("https://www.google.com" + s).get();
            String url = "https://www.google.com" + s;
            Document doc = null;
            try {
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com")
                        .get();

                String s1 = String.valueOf(document.getElementsByClass("rg_meta"));
                System.out.println(s1);

            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (HttpStatusException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(webView, 800, 600);

        primaryStage.setTitle("JavaFX WebView Example");
        primaryStage.setScene(scene);
        primaryStage.show();

        */
        // can only grab first 100 results
        String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
        String url = "https://www.bing.com/images/search?q=attack+on+titan&form=HDRSC3&first=1&cw=1177&ch=894";

        List<String> resultUrls = new ArrayList<String>();

        try {
            Document doc = Jsoup.connect(url).userAgent(userAgent).referrer("https://www.bing.com/").get();

            //String s = String.valueOf(doc.getElementsByClass("FRuiCf islib nfEiy"));

            //Elements elements = doc.getElementsByClass("FRuiCf");
            Element elements = doc.getElementsByClass("iusc").get(0);
            String s = elements.select("a").attr("href");

            System.out.println("https://www.bing.com" + s);
            Document document = Jsoup.connect("https://www.bing.com" + s).userAgent(userAgent).referrer("https://www.bing.com/").get();
            System.out.println(document);
            /*Element element = document.getElementsByClass("imgContainer").get(0);

            Element imageElement = element.select("img").first();
            assert imageElement != null;
            String absoluteUrl = imageElement.absUrl("src");  //absolute URL on src
            String srcValue = imageElement.attr("src");  // exact content value of the attribute.
            System.out.println(srcValue);*/
            /*
            for (Element element : elements) {
                if (element.childNodeSize() > 0) {
                    String s = element.select("a").attr("href");
                    System.out.println(s);
                    resultUrls.add(s);
                }
            }

            System.out.println("number of results: " + resultUrls.size());

            for (String imageUrl : resultUrls) {
                System.out.println(imageUrl);
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}