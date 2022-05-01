import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class FileReadingTest {

    ClassLoader cl = FileReadingTest.class.getClassLoader();

    @Test
    void readZipFileTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/filesSamples.zip"));
        ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("filesSamples.zip"));
        ZipEntry entry;

        while ((entry = is.getNextEntry()) != null) {
            try (InputStream inputStream = zf.getInputStream(entry)) {
                if (entry.getName().equals("dogs.csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    List<String[]> content = reader.readAll();
                    org.assertj.core.api.Assertions.assertThat(content).contains(
                            new String[]{"Dog", "Breed"});
                }

                if (entry.getName().equals("books.xlsx")) {
                    XLS xls = new XLS(inputStream);
                    String stringCellValue = xls.excel.getSheetAt(0).getRow(5).getCell(1).getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Былины");
                }

                if (entry.getName().equals("Как_справиться_с_трудной_дилеммой_docx.pdf")) {
                    PDF pdf = new PDF(inputStream);
                    Assertions.assertEquals(3, pdf.numberOfPages);
                    assertThat(pdf, new ContainsExactText("Поймите, что идеального решения нет"));
                }
            }
        }
    }
}

