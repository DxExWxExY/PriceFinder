package dxexwxexy.pricefinder.Data;

import android.os.Handler;
import android.os.Message;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDataFinder {

    private String url;
    private String store;
    private double initialPrice, currentPrice;
    private Handler handler;
    private boolean fetched;
    private static final String AMAZON_ID = ".a-section .a-spacing-none";
    private static final String EBAY_ID = ".notranslate";

    ItemDataFinder(String url) {
        this.url = url;
        setStore();
        handler = new Handler(msg -> {
            switch (msg.arg1) {
                case 1:
                    initialPrice = -1;
                    currentPrice = -1;
                    fetched = true;
                    return true;
                default:
                    if (fetched) {
                        currentPrice = (double) msg.obj;
                    } else {
                        initialPrice = (double) msg.obj;
                        currentPrice = initialPrice;
                        fetched = true;
                    }
                    return true;
            }
        });
        fetchPrices();
    }

    ItemDataFinder(String url, double initialPrice) {
        this.url = url;
        this.initialPrice = initialPrice;
        setStore();
        handler = new Handler(msg -> {
            switch (msg.arg1) {
                case 1:
                    currentPrice = -1;
                    fetched = true;
                    return true;
                default:
                    if (fetched) {
                        currentPrice = (double) msg.obj;
                    } else {
                        currentPrice = (double) msg.obj;
                        fetched = true;
                    }
                    return true;
            }
        });
        fetchPrices();
    }

    private void fetchPrices() {
        switch (store) {
            case "amazon":
                getFromStore(AMAZON_ID);
                break;
            case "ebay":
                getFromStore(EBAY_ID);
                break;
            default:
                getFromStore("utep");
                break;
        }
    }

    private void setStore() {
        if (url.matches("\\S+" + "amazon" + "\\S+")) {
            this.store = "amazon";
        } else if (url.matches("\\S+" + "ebay" + "\\S+")) {
            this.store = "ebay";
        } else {
            this.store = "utep";
        }
    }

    public String getStore() {
        return store;
    }

    private void getFromStore(String identifier) {
        new Thread(() -> {
            try {
                Message message = new Message();
                Document document = Jsoup.connect(url).get();
                String question;
                if (identifier.equals("utep")) {
                    question = document.toString();
                } else {
                    question = document.select(identifier).first().text();
                }
                Pattern pattern = Pattern.compile("\\$\\d+\\.\\d+");
                Matcher matcher = pattern.matcher(question);
                if (matcher.find()) {
                    message.obj = Double.parseDouble(matcher.group(0).substring(1));
                }
                handler.sendMessage(message);
            } catch (IllegalArgumentException | NullPointerException | IOException e) {
                Message message = new Message();
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

    public double getInitialPrice() {
        if (fetched) {
            return initialPrice;
        }
        return 0;
    }

    public double getCurrentPrice() {
        if (fetched) {
            fetchPrices();
            return currentPrice;
        }
        return 0;
    }

    public boolean isFetched() {
        return fetched;
    }
}
