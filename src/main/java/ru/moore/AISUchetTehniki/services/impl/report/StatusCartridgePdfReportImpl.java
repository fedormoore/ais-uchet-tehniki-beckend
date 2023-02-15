package ru.moore.AISUchetTehniki.services.impl.report;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgHistoryRepository;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.report.StatusCartridgePdfReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusCartridgePdfReportImpl implements StatusCartridgePdfReport {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgHistoryRepository materialValueOrgHistoryRepository;

    private Resource loadEmployeesWithClassPathResource() {
        return new ClassPathResource("fonts/arial.ttf");
    }

    @Override
    public ByteArrayInputStream createReport(List<String> status) {
        try {
            PdfFont font = PdfFontFactory.createFont(loadEmployeesWithClassPathResource().getURI().toString());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
            pdfDoc.addNewPage();
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdfDoc);

            List<MaterialValueOrg> materialValueOrgList = new ArrayList<>();
            if (status.size() == 0) {
                materialValueOrgList = materialValueOrgRepository.findAllByMaterialValueMaterialValueTypeNameOrderByStatusAsc("Картридж");
            } else {
                materialValueOrgList = materialValueOrgRepository.findAllByMaterialValueMaterialValueTypeNameAndStatusInOrderByStatusAsc("Картридж", status);
            }

            String statusMaterialValueOrg = "";
            for (MaterialValueOrg materialValueOrg : materialValueOrgList) {
                Table table = new Table(new float[]{500, 50, 50});

                if (!statusMaterialValueOrg.equals(materialValueOrg.getStatus())) {
                    if (!statusMaterialValueOrg.equals("")) {
                        doc.add(new Paragraph());
                    }
                    Paragraph locationParagraph = new Paragraph();
                    locationParagraph.setFont(font);
                    locationParagraph.add("Статус " + materialValueOrg.getStatus());
                    locationParagraph.setFontSize(12);
                    locationParagraph.setTextAlignment(TextAlignment.LEFT);
                    doc.add(locationParagraph);
                    doc.add(new Paragraph());

                    statusMaterialValueOrg = materialValueOrg.getStatus();

                    table.addCell(new Cell().add(new Paragraph("Наименование").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                    table.addCell(new Cell().add(new Paragraph("Штрихкод").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                    table.addCell(new Cell().add(new Paragraph("Количество заправок").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                }

                table.addCell(createCall(font, materialValueOrg.getMaterialValue().getNameFirm() + " " + materialValueOrg.getMaterialValue().getNameModel()));
                table.addCell(createCall(font, materialValueOrg.getBarcode()));

                int countRefilling = 0;
                countRefilling = materialValueOrgHistoryRepository.countByTypeAndMaterialValueOrgId("CARTRIDGE_REFILL", materialValueOrg.getId());
                table.addCell(createCall(font, String.valueOf(countRefilling)));

                doc.add(table);
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
