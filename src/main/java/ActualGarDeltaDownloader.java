import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class ActualGarDeltaDownloader {
    private static final int CONNECT_TIMEOUT = 2000;
    private static final int READ_TIMEOUT = 2000;

    String userHomeDir = System.getProperty("user.home");
    private String urlPath = "https://fias.nalog.ru/Public/Downloads/Actual/gar_delta_xml.zip";
    private String filePath = userHomeDir +File.separator+ "gar" +File.separator+ "gar_delta_xml.zip";

    public ActualGarDeltaDownloader() {
    }

    public ActualGarDeltaDownloader(String urlPath, String filePath) {
        this.urlPath = urlPath;
        this.filePath = filePath;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void download() throws IOException, InterruptedException {
        long fileSize = checkFileSize(filePath);
        long urlFileSize = checkFileSizeAtUrl(urlPath);

        if (fileSize==urlFileSize) {
            System.out.println("Актуальная версия обновления уже загружена");
        } else if (fileSize!=0) {
            System.out.println("Загружена устаревшая версия обновления, начинаем загрузку");
            utilsDownload();
            System.out.println("Загрузка завершена!");
        } else {
            System.out.println("Загрузка обновления...");
            utilsDownload();
            System.out.println("Загрузка завершена!");
        }
    }

    private void utilsDownload() throws IOException {
        URL source = new URL(urlPath);
        File destination = new File(filePath);
        final URLConnection connection = source.openConnection();

        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        try (final InputStream stream = connection.getInputStream()) {
            copyInputStreamToFile(stream, destination);
        } catch (IOException e) {
            System.out.println("Запись в файл невозможна");
        }

    }

    private long checkFileSizeAtUrl(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("HEAD");
        return httpConnection.getContentLengthLong();
    }

    private long checkFileSize(String filePath) {
        File garFile;
        long fileSize = 0;

        try {
            garFile = new File(filePath);
            fileSize = FileUtils.sizeOf(garFile);
        } catch (IllegalArgumentException e) {
            System.out.println("Файл не существует");
        }

        return fileSize;
    }
}
