package uz.pdp.downloader;

import uz.pdp.config.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoDownloader {

    public static String download(String url, String prefix, long chatId) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outputFilePath = ConfigLoader.get("download.folder") + File.separator +
                prefix + "_chat" + chatId + "_" + timestamp + ".mp4";

        ProcessBuilder builder = new ProcessBuilder(
                ConfigLoader.get("yt.dlp.path"),
                "-f", "mp4",
                "-o", outputFilePath,
                url
        );

        builder.redirectErrorStream(true); // chiqish + xatoni birga olish

        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                File file = new File(outputFilePath);
                if (file.exists()) {
                    return outputFilePath;
                } else {
                    System.err.println("Fayl topilmadi, garchi exitCode=0 bo‘lsa ham.");
                    return null;
                }
            } else {
                System.err.println("yt-dlp xatolik bilan yakunlandi. Exit code: " + exitCode);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Yuklab olishda xatolik: " + e.getMessage());
            return null;
        }
    }
}
