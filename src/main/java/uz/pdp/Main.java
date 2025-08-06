package uz.pdp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.bot.DownloadBot;

@SuppressWarnings("ALL")
public class Main {
    public static void main(String[] args) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        DownloadBot downloadBot = new DownloadBot();
        botsApi.registerBot(downloadBot);
        System.out.println("Bot ishga tushdi");
    }
}