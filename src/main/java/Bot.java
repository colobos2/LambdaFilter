import actions.Filters;
import actions.Operation;
import commands.AppBotCommands;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import utils.PictureUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    HashMap<String, Message> messages = new HashMap<>();
    String fileName = "received_image.jpeg";
    int methodIndex = 0;
    Message message;

    @Override
    public void onUpdateReceived(Update update) {
        message = update.getMessage();
        if (message.getText() != null) {
            if (message.getText().equals("Стоп")) {
                stopBot();

            } else
            runPhotoFilter(message);
        } else if (message.getPhoto() != null) {
            try {
                execute(getMessage(message));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public void startBotMessage(Message message){
        SendMessage startBot = new SendMessage();
        startBot.setChatId(message.getChatId().toString());
        startBot.setText("Для возобновления работы напишите \"Старт\"");
    }

    void stopBot() {
        Main.botSession.stop();
    }

    private SendMessage getMessage(Message message) {
        PhotoSize photoSize = message.getPhoto().get(2);
        final String fileILD = photoSize.getFileId();
        try {
            final File file = sendApiMethod(new GetFile(fileILD));
            final String imageUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
            PictureUtil.saveImage(imageUrl, fileName);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
        messages.put(message.getChatId().toString(), message);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(getKeyboardFilter());
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Выберите фильтр");
        return sendMessage;
    }

    private void runPhotoFilter(Message message) {
        String text = message.getText();
        Operation operation = PictureUtil.getOperation(text);
        if (operation == null) {

        } else {
            PictureUtil.processingImage(fileName, operation);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(message.getChatId().toString());
            InputFile newFile = new InputFile();
            newFile.setMedia(new java.io.File(fileName));
            sendPhoto.setPhoto(newFile);
            sendPhoto.setCaption("Edited image");
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ReplyKeyboardMarkup getKeyboardFilter() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        Method[] methods = Filters.class.getMethods();
        List<AppBotCommands> commands = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(AppBotCommands.class)) {
                commands.add(method.getAnnotation(AppBotCommands.class));

            }
        }
        int columnCount = 3;
        int rowsCount = (int) (Math.ceil((double) commands.size() / columnCount));

        for (int rowIndex = 0; rowIndex < rowsCount; rowIndex++) {
            KeyboardRow row = new KeyboardRow();
            for (int columnIndex = 0; columnIndex < columnCount && methodIndex < commands.size(); columnIndex++) {
                AppBotCommands command = commands.get(methodIndex);
                methodIndex++;
                KeyboardButton keyboardButton = new KeyboardButton(command.name());
                row.add(keyboardButton);
            }
            keyboardRows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return "newBot249579234_bot";
    }

    @Override
    public String getBotToken() {
        return "7443134916:AAG7FnOXmySb_GGRSmrdciNxLeQKtEwsAlU";
    }

}


