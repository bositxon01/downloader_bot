package uz.pdp.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import uz.pdp.bot.DownloadBot;
import uz.pdp.downloader.VideoDownloader;
import uz.pdp.util.UrlUtils;

@SuppressWarnings("ALL")
public class VideoDownloadTask implements Runnable {
    private final String url;
    private final long chatId;
    private final DownloadBot bot;

    public VideoDownloadTask(String url, long chatId, DownloadBot bot) {
        this.url = url;
        this.chatId = chatId;
        this.bot = bot;
    }

    @Override
    public void run() {
        // "📥 Yuklanmoqda..." xabarini yuborish va xabar ID sini olish
        Message loadingMessage = bot.sendText(chatId, "📥 Video yuklanmoqda. Iltimos, biroz kuting...");

        // Platformani aniqlash
        String prefix;
        if (UrlUtils.isInstagramUrl(url)) {
            prefix = "insta";
        } else if (UrlUtils.isYoutubeUrl(url)) {
            prefix = "yt";
        } else {
            bot.sendText(chatId, "❌ Noma’lum platforma. Faqat YouTube va Instagram qo‘llab-quvvatlanadi.");
            return;
        }

        // Yuklab olish
        String filePath = VideoDownloader.download(url, prefix, chatId);

        if (filePath != null) {
            bot.sendVideo(chatId, filePath);
            bot.deleteFile(filePath);
        } else {
            bot.sendText(chatId, "❌ Video yuklab olinmadi. Havola noto‘g‘ri bo‘lishi mumkin.");
        }

        // Avval yuborilgan "yuklanmoqda" xabarini o‘chirish
        if (loadingMessage != null) {
            bot.deleteMessage(chatId, loadingMessage.getMessageId());
        }
    }
}

