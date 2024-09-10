import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.Set;

import static java.lang.Thread.currentThread;

public class Main {
    static BotSession botSession;

    public static void main(String[] args) throws TelegramApiException, InterruptedException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botSession = api.registerBot(bot);

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println(threadSet);
        synchronized  (currentThread()){
        currentThread().wait();
            DefaultBotSession.class.notify();
        }
        System.out.println("Конец");
    }
}

