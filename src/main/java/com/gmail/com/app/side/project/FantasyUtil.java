package com.gmail.com.app.side.project;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

class FantasyUtil {

    private static final String NBA_COM_PLAYERS_IMAGE_URL = "https://ak-static.cms.nba.com/wp-content/uploads/headshots/nba/latest/260x190/";
    private static final String NBA_COM_PLAYERS_IMG_SUFFIX = ".png";
    private static final String HTTPS_WWW_NBA_COM_PLAYERS = "https://www.nba.com/players/";

    static String getNbaComImgHref(JSONObject ele) {
        return NBA_COM_PLAYERS_IMAGE_URL + ele.get("id") + NBA_COM_PLAYERS_IMG_SUFFIX;
    }

    static JSONArray getNbaComPlayers() {
        Elements playersAElement = getElements(HTTPS_WWW_NBA_COM_PLAYERS, "a.playerList");
        JSONArray jsonArray = new JSONArray();
        playersAElement.stream()
                .map(a -> a.tagName("a"))
                .forEach(a -> {
                    JSONObject element = new JSONObject();
                    element.put("name", getTitle(a, "title"));
                    element.put("id", getNbaComId(a));
                    jsonArray.add(element);
                });

        System.out.println("nameId = " + jsonArray);
        return jsonArray;
    }

    private static String getTitle(Element a, String title) {
        String attr = a.attr(title);
        return attr.substring(1, attr.length() - 1);
    }

    private static Elements getElements(String url, String cssQuery) {
        Document doc = connect(url);
        assert doc != null;
        return doc.select(cssQuery);
    }

    private static  Document connect(String url) {
        try {
            File input = new File("D:\\Downloads\\nba.html");
            return Jsoup.parse(input, "UTF-8", "http://example.com/");

//            return Jsoup.connect(url).userAgent("Mozilla")
//                    .timeout(5000)
//                    .cookie("cookiename", "val234")
//                    .cookie("anothercookie", "ilovejsoup")
//                    .referrer("http://google.com")
//                    .header("headersecurity", "xyz123").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getNbaComId(Element a) {
        String elementContainingId = a.attr("href");
        return elementContainingId.substring(elementContainingId.lastIndexOf("/") + 1);
    }

}
