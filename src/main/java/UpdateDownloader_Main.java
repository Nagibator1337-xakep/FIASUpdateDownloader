import java.io.IOException;

public class UpdateDownloader_Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String userHomeDir = System.getProperty("user.home");
        String FILE_PATH = userHomeDir + "\\Desktop\\gar\\gar_delta_xml.zip";
        String URL_PATH = "https://fias.nalog.ru/Public/Downloads/Actual/gar_delta_xml.zip";
        ActualGarDeltaDownloader downloader = new ActualGarDeltaDownloader();
        downloader.setFilePath(FILE_PATH);
        downloader.setUrlPath(URL_PATH);

        downloader.download();

        UnzipFile unzipFile = new UnzipFile(FILE_PATH);
        unzipFile.unzip();
    }
}
