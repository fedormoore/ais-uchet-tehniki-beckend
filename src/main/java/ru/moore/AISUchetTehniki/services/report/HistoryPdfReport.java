package ru.moore.AISUchetTehniki.services.report;

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
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrgHistory;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgHistoryRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryPdfReport {

    private final MaterialValueOrgService materialValueOrgService;
    private final MaterialValueOrgHistoryRepository materialValueOrgHistoryRepository;
    private final ReasonService reasonService;

    public Resource loadEmployeesWithClassPathResource() {
        return new ClassPathResource("fonts/arial.ttf");
    }

    public ByteArrayInputStream createReport(List<String> idList) {
        try {
            PdfFont font = PdfFontFactory.createFont(loadEmployeesWithClassPathResource().getURI().toString());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
            pdfDoc.addNewPage();
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdfDoc);

            for (int i = 0; i < idList.size(); i++) {
                MaterialValueOrg materialValueOrg = materialValueOrgService.findById(UUID.fromString(idList.get(i))).orElse(null);

                Paragraph nameMaterialValue = new Paragraph();
                nameMaterialValue.setFont(font);
                nameMaterialValue.add(materialValueOrgService.getName(materialValueOrg));
                nameMaterialValue.setFontSize(12);
                doc.add(nameMaterialValue);
                doc.add(new Paragraph());

                List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByMaterialValueOrgIdOrderByCreatedAtAsc(UUID.fromString(idList.get(i)));
                Table table = new Table(new float[]{80, 60, 400});
                table.addCell(new Cell().add(new Paragraph("Дата").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("Значение").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("Основание").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                for (MaterialValueOrgHistory materialValueOrgHistory : materialValueOrgHistoryList) {
                    String pattern = "dd.MM.yyyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    Paragraph typeString = new Paragraph();
                    typeString.setFont(font);
                    typeString.add(simpleDateFormat.format(materialValueOrgHistory.getCreatedAt()));
                    typeString.setFontSize(12);

                    Cell cell = new Cell();
                    cell.add(typeString).setTextAlignment(TextAlignment.LEFT);

                    cell.setPaddingTop(3);
                    cell.setPaddingRight(5);
                    cell.setPaddingBottom(3);
                    cell.setPaddingLeft(5);

                    table.addCell(cell);

                    table.addCell(createBarcode(font, materialValueOrgHistory));

                    Paragraph reasonString = new Paragraph();
                    if (materialValueOrgHistory.getReason() != null) {
                        Reason statement = reasonService.findById(materialValueOrgHistory.getReason().getId()).orElse(null);

                        reasonString.setFont(font);
                        reasonString.add("Заявление от "+simpleDateFormat.format(statement.getDate())+" номер "+statement.getNumber());
                        reasonString.setFontSize(12);
                    }
                    Cell cell1 = new Cell();
                    cell1.add(reasonString).setTextAlignment(TextAlignment.LEFT);
                    table.addCell(cell1);
                }
                doc.add(table);

                if (idList.size() > (i + 1)) {
                    doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }

            doc.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cell createBarcode(PdfFont font, MaterialValueOrgHistory materialValueOrgHistory) {
        Paragraph typeString = new Paragraph();
        typeString.setFont(font);
        typeString.add(materialValueOrgHistory.getType());
        typeString.setFontSize(12);

        Paragraph newValueString = new Paragraph();
        if (materialValueOrgHistory.getNewValue() != null) {
            newValueString.setFont(font);
//            newValueString.add(materialValueOrgHistory.getNewValue());
            newValueString.setFontSize(12);
        }

        Cell cell = new Cell();
        cell.add(typeString).setTextAlignment(TextAlignment.LEFT);
        cell.add(newValueString).setTextAlignment(TextAlignment.LEFT);

        cell.setPaddingTop(3);
        cell.setPaddingRight(5);
        cell.setPaddingBottom(3);
        cell.setPaddingLeft(5);

        return cell;
    }

}
