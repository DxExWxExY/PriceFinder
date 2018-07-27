package dxexwxexy.pricefinder.Data;

import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDataFinder {

    private String url;
    private String store;
    private double initialPrice, currentPrice;
    private Handler handler;
    private boolean fetched;
    private static final String AMAZON_ID = ".sims-fbt-checkbox-label";
    private static final String AMAZON = "amazon";


    ItemDataFinder(String url) {
        this.url = url;
        // TODO: 7/27/2018 undo hardcode
        store = AMAZON;
        handler = new Handler(msg -> {
            if (fetched) {
                currentPrice = (double) msg.obj;
            } else {
                initialPrice = (double) msg.obj;
                System.out.println("msg =============================== "+ msg.obj+ "==");
                fetched = true;
            }
            return true;
        });
        getPrice();
    }

    /**
     * Simulates a price generation using a range and an initial price.
     * @param initial Initial price of the item.
     * @return Randomized price from range.
     */
    public double updatePrice(double initial) {
        return Math.random() * ((initial*2 - initial/2) + 1) + (initial/2);
    }

    public double getPrice() {
        switch (store) {
            case AMAZON:
                getFromStore(AMAZON_ID);
                break;
        }
        return currentPrice;
    }

    private void setStore() {
        if (url.matches(AMAZON)) {
            store = AMAZON;
        }
    }

    public String getStore() {
        return store;
    }

    private void getFromStore(String identifier) {
        new Thread(() -> {
            Message message = new Message();
            try {
                Document document;
                document = Jsoup.connect(url).get();
                String question = document.select(identifier).first().text();
                Pattern pattern = Pattern.compile("\\$\\d+\\.\\d+");
                Matcher matcher = pattern.matcher(question);
                if (matcher.find()) {
                    message.obj = (Object) Double.parseDouble(matcher.group(0).substring(1));
                }
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public double getInitialPrice() {
        if (fetched) {
            return initialPrice;
        }
        return -1.0;
    }
}
