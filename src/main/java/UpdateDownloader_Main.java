import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpdateDownloader_Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String userHomeDir = System.getProperty("user.home");
        String filePath = userHomeDir +File.separator+ "gar" +File.separator+ "gar_delta_xml.zip";
        String dirPath = userHomeDir +File.separator+ "gar" +File.separator+ "gar_delta_xml";

        String FIAS_JSON_REQUEST_URL = "https://fias.nalog.ru/WebServices/Public/GetLastDownloadFileInfo";
        String UPDATE_JSON_KEY = "GarXMLDeltaURL";

        System.out.println("Добро пожаловать в менеджер загрузки обновлений ГАР БД ФИАС");
        System.out.println("Для начала загрузки обновления нажмите Enter...");
        pressEnterToContinue();

        System.out.println("Загрузка осуществляется в папку: " + userHomeDir +File.separator+ "gar");

        String garUpdateLink = JsonProcessing.getUpdateLink(FIAS_JSON_REQUEST_URL, UPDATE_JSON_KEY);

        ActualGarDeltaDownloader downloader = new ActualGarDeltaDownloader();
        downloader.setFilePath(filePath);
        downloader.setUrlPath(garUpdateLink);

        downloader.download();

        System.out.println("Для начала распаковки архива нажмите Enter...");
        pressEnterToContinue();

        UnzipFile unzipFile = new UnzipFile(filePath);
        unzipFile.unzip();

        File directoryPath = new File(dirPath);

        List<File> files = new ArrayList<>();
        File[] filesList = directoryPath.listFiles();
        for(File file : Objects.requireNonNull(filesList)) {
            if (file.isDirectory()) {
                File subdirFile = new File(file.getAbsolutePath());
                File[] subList = subdirFile.listFiles();
                files.addAll(Arrays.asList(Objects.requireNonNull(subList)));
            } else {
                files.add(file);
            }
        }

        System.out.println("Для начала преобразования файлов XML в CSV нажмите Enter...");
        pressEnterToContinue();

        int howManyFiles = files.size();
        int filesCounter = 1;
        for (File file : files) {
            XmlToCsvConverter xmlToCsvConverter = new XmlToCsvConverter();
            xmlToCsvConverter.readXml(file.getAbsolutePath());


            int garIndex = dirPath.length();
            String csvStr = dirPath.substring(0,garIndex-14)
                    +File.separator+ "csv" +File.separator+
                    file.getAbsolutePath().substring(garIndex+1,file.getAbsolutePath().length()-4)
                    +".csv";

            System.out.print(" | Преобразование файла: "+file.getName().substring(0,20)
                    +"....CSV ("+filesCounter+" из "+howManyFiles+")");
            System.out.print("                                           \r");

            xmlToCsvConverter.buildCSV(csvStr);
            filesCounter++;
        }
        System.out.println("\nПреобразование завершено, хорошего дня!");
        System.out.println("Для выхода из программы нажмите Enter");
        pressEnterToContinue();
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
