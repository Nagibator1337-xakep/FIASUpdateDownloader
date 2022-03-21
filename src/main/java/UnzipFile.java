import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFile {
    private final String userHomeDir = System.getProperty("user.home");
    private String zipFilePath = userHomeDir + "\\Desktop\\gar\\gar_delta_xml.zip";
    private final String zipDirPath = zipFilePath.substring(0,zipFilePath.length()-4);

    public UnzipFile() {
    }

    public UnzipFile(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public String getFilePath() {
        return zipFilePath;
    }

    public void setFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public void unzip() throws IOException {
        File destDir = new File(zipDirPath);
        byte[] buffer = new byte[1024];

        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    System.out.println("Невозможно создать папку");
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                System.out.println("Распаковка файла: "+newFile.getName());
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        System.out.println("Распаковка завершена");
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
