import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    String[] comands = {"/msgToAll ", "/city "};

    private static final Logger log = Logger.getLogger(Bot.class);

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            sendMessage(sendMessage);
            log.info("Сообщение отправлено пользователю " + message.getChatId().toString());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("Ошибка отправки сообщения! Пользователь " + message.getChatId().toString());
        }
    }

    public void sendMsgToAll(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("Ошибка отправки сообщения всем пользователям!");
        }
    }

    public void sendStk(Message message, String sticker) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(message.getChatId());
        sendSticker.setSticker(sticker);
        try {
            sendSticker(sendSticker);
            log.info("Стикер отправлен пользователю " + message.getChatId().toString());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("Ошибка отправки стикера!");
        }
    }


    public void onUpdateReceived(Update update) {
        Model model = new Model();
        UserBot userBot = new UserBot();
        Message message = update.getMessage();

        String source1, source2;
        source1 = message.getText();
        source2 = source1;

        String[] parts = source1.split(" ");
        String res = parts[0] + " ";

        for (int i = 0; i < comands.length; i++) {
            if (res.equals(comands[i])) {
                source1 = source1.replace(comands[i], "");
                break;
            }
        }

        if (source1.equals(source2)) {
            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Привет, " + userBot.getUserInfo(message) + "!\n" +
                            "Этот бот может узнавать погоду в твоем городе прямо сейчас, просто введи \"/city Название города\" и узнавай " +
                            "погоду в своем городе одной кнопкой!\n" +
                            "Также ты можешь ввести любой город и узнать погоду в нем прямо сейчас!\n" +
                            "\"/help\" - помощь пользователям");
                    sendStk(message, "https://cdn.tlgrm.ru/stickers/494/75e/49475e41-da6f-4cb0-8810-9b337286b539/192/5.webp");
                    userBot.userInfoToBD(message);
                    log.info("Добавлен новый пользователь - " + message.getChatId().toString());
                    break;
                case "/help":
                    sendMsg(message, "Этот бот может узнавать погоду в твоем городе прямо сейчас, просто введи \"/city Название города\" и узнавай " +
                            "погоду в своем городе одной кнопкой!\n" +
                            "Также ты можешь ввести любой город и узнать погоду в нем прямо сейчас!\n" +
                            "Для дополнительных вопросов писать - @rever_v");
                    log.info("Запрос помощи от пользователя - " + message.getChatId().toString());
                    break;
                case "Узнать погоду в своем городе":
                    String chatId = message.getChatId().toString();
                    try {
                        sendMsg(message, Weather.getWeather(userBot.getUserCity(chatId), model));
                        sendStk(message, Weather.getWeatherToStickers(userBot.getUserCity(chatId), model));
                        log.info("Информирования пользователя " + message.getChatId().toString() + " по кнопке");
                    } catch (IOException e) {
                        sendMsg(message, "Город не установлен\n" +
                                "Используйте \"/city Название города\" чтобы установить свой город");
                        log.error("Нажатие на кнопку при не сохраненном городе");
                    }
                    break;
                case "Привет":
                    log.info("Попытка ввести \"Привет\"");
                    break;
                default:
                    try {
                        sendMsg(message, Weather.getWeather(message.getText(), model));
                        sendStk(message, Weather.getWeatherToStickers(message.getText(), model));
                        log.info("Пользователь " + message.getChatId().toString() + " узнал погоду в городе - " + message.getText());
                    } catch (IOException e) {
                        sendMsg(message, "Такой город не найден :(");
                        log.error("Не верный запрос " + message.getText() + ", от  пользователя " + message.getChatId().toString());
                    }
            }
        } else {
            source2 = source2.replace(source1, "");
            switch (source2) {
                case "/msgToAll ":
                    userBot.messageToUsers(source1);
                    log.info("Информированяие всех пользователей от " + userBot.getUserInfo(message) + " " + message.getChatId().toString());
                    break;
                case "/city ":
                    userBot.userCityToDB(message, source1);
                    try {
                        Weather.getWeather(source1, model);
                        sendMsg(message, "Город успешно установлен");
                        log.info("Пользователь " + message.getChatId().toString() + " обновил город в БД");
                    } catch (IOException e) {
                        sendMsg(message, "Такой город не найден, поробуйте снова...");
                        log.error("Неверный город для установки");
                    }
                    break;
            }
        }
    }


    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);


        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirtsRow = new KeyboardRow();

        keyboardFirtsRow.add(new KeyboardButton("Узнать погоду в своем городе"));


        keyboardRowList.add(keyboardFirtsRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }


    public String getBotUsername() {
        return "WeatherLovesYouBot";
    }

    public String getBotToken() {
        return "1829811897:AAETCySwkl9pN9IHZb3rrSSMlupIyWVzmfE";
    }
}
