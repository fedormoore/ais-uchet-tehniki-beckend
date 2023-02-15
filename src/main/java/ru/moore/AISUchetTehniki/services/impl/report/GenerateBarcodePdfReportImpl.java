package ru.moore.AISUchetTehniki.services.impl.report;

import com.itextpdf.barcodes.BarcodePDF417;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.report.GenerateBarcodePdfReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateBarcodePdfReportImpl implements GenerateBarcodePdfReport {

    private final MaterialValueOrgService materialValueOrgService;

    private Resource loadEmployeesWithClassPathResource() {
        return new ClassPathResource("fonts/arial.ttf");
    }

    @Override
    public ByteArrayInputStream createReport(int size, List<String> idList) {
        try {
            PdfFont font = PdfFontFactory.createFont(loadEmployeesWithClassPathResource().getURI().toString());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdfDoc);

            int sizeWidthBarcode = 15;
            sizeWidthBarcode = sizeWidthBarcode * size;
            int sizeWidthPage = 500;
            int countColumns = sizeWidthPage / sizeWidthBarcode;
            float columnWidth[] = new float[countColumns];
            for (int i = 0; i < columnWidth.length; i++) {
                columnWidth[i] = sizeWidthBarcode;
            }

            Table table = new Table(columnWidth);

            for (String city : idList) {
                MaterialValueOrg materialValueOrg = materialValueOrgService.findById(UUID.fromString(city)).orElse(null);
                table.addCell(createBarcode(size, font, materialValueOrg.getBarcode(), materialValueOrgService.getName(materialValueOrg), pdfDoc));
            }

            doc.add(table);
            doc.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cell createBarcode(int size, PdfFont font, String code, String name, PdfDocument pdfDoc) {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(code);
        Image image = new Image(barcode.createFormXObject(pdfDoc));
//      соотношение сторон 7,50:1
        int sizeWidth = 15;
        int sizeHeight = 2;
        sizeWidth = sizeWidth * size;
        sizeHeight = sizeHeight * size;
        image.scaleAbsolute(sizeWidth, sizeHeight);

        Paragraph nameString = new Paragraph();
        nameString.setFont(font);
        nameString.add(name);
        nameString.setFontSize(1 * size);

        Paragraph codeString = new Paragraph();
        codeString.add(code);
        codeString.setFontSize(1 * size);

        Cell cell = new Cell();
        cell.add(nameString).setTextAlignment(TextAlignment.CENTER);
        cell.add(image);
        cell.add(codeString).setTextAlignment(TextAlignment.CENTER);
        cell.setPaddingTop(3);
        cell.setPaddingRight(5);
        cell.setPaddingBottom(3);
        cell.setPaddingLeft(5);

        return cell;
    }

}
