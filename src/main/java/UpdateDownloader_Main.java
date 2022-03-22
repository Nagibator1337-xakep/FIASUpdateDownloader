import java.io.File;
import java.io.IOException;

public class UpdateDownloader_Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String userHomeDir = System.getProperty("user.home");
        String filePath = userHomeDir +File.separator+ "gar" +File.separator+ "gar_delta_xml.zip";

        String FIAS_JSON_REQUEST_URL = "https://fias.nalog.ru/WebServices/Public/GetLastDownloadFileInfo";
        String UPDATE_JSON_KEY = "GarXMLDeltaURL";

//        ActualGarDeltaDownloader downloader = new ActualGarDeltaDownloader();
//        downloader.setFilePath(FILE_PATH);
//        downloader.setUrlPath(URL_PATH);

//        downloader.download();

//        UnzipFile unzipFile = new UnzipFile(FILE_PATH);
//        unzipFile.unzip();

        System.out.println("Добро пожаловать в менеджер загрузки обновлений ГАР БД ФИАС");
        System.out.println("Для начала загрузки обновления нажмите Enter...");
        pressEnterToContinue();

        String garUpdateLink = JsonProcessing.getUpdateLink(FIAS_JSON_REQUEST_URL, UPDATE_JSON_KEY);

        ActualGarDeltaDownloader downloader = new ActualGarDeltaDownloader();
        downloader.setFilePath(filePath);
        downloader.setUrlPath(garUpdateLink);

        downloader.download();

        System.out.println("Для начала распаковки архива нажмите Enter...");
        pressEnterToContinue();

        UnzipFile unzipFile = new UnzipFile(filePath);
        unzipFile.unzip();


    }

    private static void pressEnterToContinue()
    {
        try
        {
            System.in.read();
            System.in.skip(System.in.available());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
