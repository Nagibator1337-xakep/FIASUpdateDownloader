import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class XmlToCsvConverter {

    private final List<List<String>> dataRowsNames = new ArrayList<>();
    private final List<List<String>> dataRowsValues = new ArrayList<>();
    private String xmlString = "";
    private String docRootElement = "";
    private final Pattern pattern = Pattern.compile("[^\\w-:.]*");


    public XmlToCsvConverter() {
    }

    public void readXml(String filePath) throws IOException {
        Path fileName = Path.of(filePath);
        byte[] content = Files.readAllBytes(fileName);

        String xmlString = new String(content, StandardCharsets.UTF_8);

        xmlString = xmlString.replaceFirst("^\uFEFF", "");

        this.xmlString = xmlString;

        Document doc = null;
        try {

            StringReader sr = new StringReader(xmlString);
            InputSource is = new InputSource(sr);

            SAXBuilder saxBuilder = new SAXBuilder();
            doc = saxBuilder.build(is);
            Element classElement = doc.getRootElement();

            List<Element> elementsList = classElement.getChildren();

            elementsList.forEach(element -> {
                List<Attribute> attributeList = element.getAttributes();

                List<String> names = new ArrayList<>();
                List<String> values = new ArrayList<>();

                attributeList.forEach(attribute -> {
                    names.add(attribute.getName());
                    values.add(attribute.getValue());
                });
                dataRowsNames.add(names);
                dataRowsValues.add(values);
            });

            normalizeData();

        } catch (IOException | JDOMException e) {
            e.printStackTrace();
            System.out.println("Ошибка обработки файла XML");
        }

        docRootElement = Objects.requireNonNull(doc).getRootElement().getName();
    }

    public void buildCSV(String strPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (!dataRowsNames.isEmpty()) {
            List<String> header = dataRowsNames.get(0);

            for (int i = 0; i < header.size(); i++) {
                sb.append(header.get(i));
                if (i != header.size() - 1) sb.append(',');
            }

            sb.append("\r\n");

            for (int j = 0; j < dataRowsValues.size(); j++) {
                List<String> row = dataRowsValues.get(j);
                for (int i = 0; i < row.size(); i++) {
                    String element = row.get(i);
                    if (isTextString(element)) {
                        sb.append("\"").append(element).append("\"");
                    } else sb.append(element);
                    if (i != row.size() - 1) sb.append(',');
                }
                if (j != dataRowsValues.size() - 1) sb.append("\r\n");
            }
        } else sb.append(docRootElement);

        Path path = Paths.get(strPath);
        new File(path.getParent().toString()).mkdirs();
        Files.writeString(path, sb.toString());
    }

    public String getXmlString() {
        return xmlString;
    }

    private boolean isTextString(String str) {
        if (str == null) {
            return false;
        }
        return pattern.matcher(str).matches();
    }

    private void normalizeData() {
        if (!dataRowsNames.isEmpty()) {
            int size = dataRowsNames.get(0).size();
            List<Integer> indexes = new ArrayList<>();

            for (int i = 1; i < dataRowsNames.size(); i++) {
                if (dataRowsNames.get(i).size() != size && dataRowsNames.get(i).size() < size) {
                    indexes.add(i);
                }
            }

            if (!indexes.isEmpty()) {
                for (Integer integer : indexes) {
                    List<String> rowName = dataRowsNames.get(integer);
                    List<String> rowValue = dataRowsValues.get(integer);
                    for (int i = 0; i < rowName.size(); i++) {
                        String string = rowName.get(i);
                        if (!Objects.equals(dataRowsNames.get(0).get(i), string)) {
                            rowName.add(i, dataRowsNames.get(0).get(i));
                            rowValue.add(i, "null");
                        }
                    }
                }
            }
        }
    }



}
