package ru.moore.AISUchetTehniki.services.impl.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.spr.LocationRepository;
import ru.moore.AISUchetTehniki.services.report.UserToLocationPdfReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserToLocationPdfReportImpl implements UserToLocationPdfReport {

    private final LocationRepository locationRepository;

    private Resource loadEmployeesWithClassPathResource() {
        return new ClassPathResource("fonts/arial.ttf");
    }

    @Override
    public ByteArrayInputStream createReport(List<UUID> idLocation) {
        System.out.println(idLocation.size());
        try {
            PdfFont font = PdfFontFactory.createFont(loadEmployeesWithClassPathResource().getURI().toString());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
            pdfDoc.addNewPage();
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdfDoc);

            List<Location> locationList = new ArrayList<>();
            if (idLocation.size() == 0) {
                locationList = locationRepository.findAll();
            } else {
                locationList = locationRepository.findAllByIdInOrderByNameAsc(idLocation);
            }

            int i = 0;
            for (Location location : locationList) {
                Paragraph locationParagraph = new Paragraph();
                locationParagraph.setFont(font);
                locationParagraph.add("Кабинет " + location.getName());
                locationParagraph.setFontSize(12);
                locationParagraph.setTextAlignment(TextAlignment.LEFT);
                doc.add(locationParagraph);
                doc.add(new Paragraph());

                Table table = new Table(new float[]{99, 190, 99, 99, 180, 180});
                table.addCell(new Cell().add(new Paragraph("E-mail").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("ФИО").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("Телефон").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("Кабинет").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("Организация").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("Должность").setFont(font)).setBackgroundColor(ColorConstants.GRAY));

                for (User user : location.getUserList()) {
                    table.addCell(createCall(font, user.getEmail()));
                    table.addCell(createCall(font, user.getLastName() + " " + user.getFirstName() + " " + user.getMiddleNames()));
                    table.addCell(createCall(font, user.getTelephone()));
                    if (user.getLocation() != null) {
                        table.addCell(createCall(font, user.getLocation().getName()));
                    } else {
                        table.addCell(createCall(font, ""));
                    }
                    if (user.getOrganization() != null) {
                        table.addCell(createCall(font, user.getOrganization().getName()));
                    } else {
                        table.addCell(createCall(font, ""));
                    }
                    table.addCell(createCall(font, user.getOrganizationFunction()));
                }

                doc.add(table);
                i++;
                if (i < locationList.size()) {
                    doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }

            doc.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cell createCall(PdfFont font, String name) {
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
