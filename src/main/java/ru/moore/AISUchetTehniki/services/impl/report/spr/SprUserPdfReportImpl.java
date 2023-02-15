package ru.moore.AISUchetTehniki.services.impl.report.spr;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.spr.UserRepository;
import ru.moore.AISUchetTehniki.services.report.spr.SprUserPdfReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprUserPdfReportImpl implements SprUserPdfReport {

    private final UserRepository userRepository;

    private static final String courier = "fonts\\arial.ttf";

    @Override
    public ByteArrayInputStream createReport() {
        try {
            PdfFont font = PdfFontFactory.createFont(courier);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
            pdfDoc.addNewPage();

            Document doc = new Document(pdfDoc);

            Paragraph nameMaterialValue = new Paragraph();
            nameMaterialValue.setFont(font);
            nameMaterialValue.add("Справочник - Сотрудники");
            nameMaterialValue.setFontSize(12);
            nameMaterialValue.setTextAlignment(TextAlignment.CENTER);
            doc.add(nameMaterialValue);
            doc.add(new Paragraph());

            Table table = new Table(new float[]{99, 190, 99, 99, 180, 180});
            table.addCell(new Cell().add(new Paragraph("E-mail").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
            table.addCell(new Cell().add(new Paragraph("ФИО").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
            table.addCell(new Cell().add(new Paragraph("Телефон").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
            table.addCell(new Cell().add(new Paragraph("Кабинет").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
            table.addCell(new Cell().add(new Paragraph("Организация").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
            table.addCell(new Cell().add(new Paragraph("Должность").setFont(font)).setBackgroundColor(ColorConstants.GRAY));

            List<User> userList = userRepository.findAll();
            for (User user : userList) {
                table.addCell(createBarcode(font, user.getEmail()));
                table.addCell(createBarcode(font, user.getLastName() + " " + user.getFirstName() + " " + user.getMiddleNames()));
                table.addCell(createBarcode(font, user.getTelephone()));
                if (user.getLocation() != null) {
                    table.addCell(createBarcode(font, user.getLocation().getName()));
                } else {
                    table.addCell(createBarcode(font, ""));
                }
                if (user.getOrganization() != null) {
                    table.addCell(createBarcode(font, user.getOrganization().getName()));
                } else {
                    table.addCell(createBarcode(font, ""));
                }
                table.addCell(createBarcode(font, user.getOrganizationFunction()));
            }

            doc.add(table);

            doc.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cell createBarcode(PdfFont font, String name) {
        Paragraph typeString = new Paragraph();
        typeString.setFont(font);
        typeString.add(name);
        typeString.setFontSize(12);
        Cell cell = new Cell();
        cell.add(typeString).setTextAlignment(TextAlignment.LEFT);
        cell.setPaddingTop(3);
        cell.setPaddingRight(5);
        cell.setPaddingBottom(3);
        cell.setPaddingLeft(5);

        return cell;
    }
}
