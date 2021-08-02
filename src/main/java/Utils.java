import java.util.HashMap;
import java.util.Map;

public class Utils {
    public final static Map<String,String> weatherStickerCodes = new HashMap<>();

    static {
        weatherStickerCodes.put("Clear", "https://tlgrm.ru/_/stickers/b23/18d/b2318d70-5188-3faf-927d-b1be87d2e83f/192/23.webp");
        weatherStickerCodes.put("Rain", "https://tlgrm.ru/_/stickers/b23/18d/b2318d70-5188-3faf-927d-b1be87d2e83f/4.webp");
        weatherStickerCodes.put("Snow", "https://tlgrm.ru/_/stickers/b23/18d/b2318d70-5188-3faf-927d-b1be87d2e83f/8.webp");
        weatherStickerCodes.put("Clouds", "https://tlgrm.ru/_/stickers/b23/18d/b2318d70-5188-3faf-927d-b1be87d2e83f/2.webp");
        weatherStickerCodes.put("Thunderstorm","https://tlgrm.ru/_/stickers/b23/18d/b2318d70-5188-3faf-927d-b1be87d2e83f/3.webp");
        weatherStickerCodes.put("Drizzle", "https://tlgrm.ru/_/stickers/b23/18d/b2318d70-5188-3faf-927d-b1be87d2e83f/19.webp");
        weatherStickerCodes.put("Fog", "https://tlgrm.ru/_/stickers/b23/18d/b2318d70-5188-3faf-927d-b1be87d2e83f/18.webp");
    }
}
