package uz.pdp.util;

public class UrlUtils {
    public static boolean isYoutubeUrl(String url) {
        return url.matches(".*(youtube\\.com|youtu\\.be).*");
    }

    public static boolean isInstagramUrl(String url) {
        return url.contains("instagram.com");
    }

    public static boolean isValidHttpUrl(String url) {
        return url != null && url.startsWith("http");
    }

    public static boolean isAllowedPlatform(String url) {
        return isYoutubeUrl(url) || isInstagramUrl(url);
    }
}
