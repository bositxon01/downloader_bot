package uz.pdp.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.config.ConfigLoader;
import uz.pdp.service.VideoDownloadTask;
import uz.pdp.util.UrlUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("ALL")
public class DownloadBot extends TelegramLongPollingBot {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override
    public String getBotUsername() {
        return ConfigLoader.get("bot.username");
    }

    @Override
    public String getBotToken() {
        return ConfigLoader.get("bot.token");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText().trim();

            if (text.startsWith("/")) {
                handleCommand(text, chatId);
                return;
            }

            if (UrlUtils.isValidHttpUrl(text)) {
                if (UrlUtils.isAllowedPlatform(text)) {
                    executor.submit(new VideoDownloadTask(text, chatId, this));
                } else {
                    sendText(chatId, "❗ Faqat YouTube va Instagram havolalariga ruxsat beriladi.");
                }
            } else {
                sendText(chatId, "ℹ️ Iltimos, YouTube yoki Instagram video havolasini yuboring.");
            }
        }
    }

    private void handleCommand(String command, long chatId) {
        switch (command.toLowerCase()) {
            case "/start" -> sendText(chatId, "👋 Salom! Men Instagram va YouTube videolarini yuklab beraman.\n" +
                    "🎥 Havolani yuboring — men sizga videoni yuboraman.");
            case "/help" ->
                    sendText(chatId, "ℹ️ Foydalanish uchun YouTube yoki Instagram videoning havolasini yuboring.\n" +
                            "❗ Fayl hajmi 50MB dan katta bo‘lsa, sizga faqat link qaytariladi.");
            default -> sendText(chatId, "❓ Noma’lum buyruq. /help buyrug‘ini sinab ko‘ring.");
        }
    }

    private boolean isAllowedUrl(String text) {
        return text.contains("youtube.com") || text.contains("youtu.be") || text.contains("instagram.com");
    }

    public Message sendText(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        try {
            return execute(msg);
        } catch (TelegramApiException e) {
            System.err.println("Matn yuborishda xatolik: " + e.getMessage());
            return null;
        }
    }

    public void deleteMessage(long chatId, int messageId) {
        try {
            DeleteMessage delete = new DeleteMessage();
            delete.setChatId(chatId);
            delete.setMessageId(messageId);
            execute(delete);
        } catch (TelegramApiException e) {
            System.err.println("Xabarni o‘chirishda xatolik: " + e.getMessage());
        }
    }

    public void sendVideo(long chatId, String videoPath) {
        File videoFile = new File(videoPath);
        if (!videoFile.exists()) {
            sendText(chatId, "❌ Video fayli topilmadi.");
            return;
        }

        long fileSizeMB = videoFile.length() / (1024 * 1024);
        if (fileSizeMB > 50) {
            sendText(chatId, "📎 Video hajmi " + fileSizeMB + " MB. Telegram cheklovi tufayli uni yubora olmayman.\n" +
                    "🔗 Video havolasi: " + videoPath);
            return;
        }

        try {
            SendVideo video = new SendVideo();
            video.setChatId(chatId);
            video.setVideo(new InputFile(videoFile));
            execute(video);
        } catch (TelegramApiException e) {
            sendText(chatId, "Videoni yuborishda xatolik yuz berdi: " + e.getMessage());
        }
    }

    public void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            System.err.println("Faylni o'chirishda muammo: " + filePath);
        }
    }
}

