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
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrgHistory;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgHistoryRepository;
import ru.moore.AISUchetTehniki.repositories.ReasonRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractPdfReport {

    private final ReasonRepository reasonRepository;
    private final MaterialValueOrgHistoryRepository materialValueOrgHistoryRepository;

    public Resource loadEmployeesWithClassPathResource() {
        return new ClassPathResource("fonts/arial.ttf");
    }

    public ByteArrayInputStream createReport(List<UUID> idContract) {
        try {
            PdfFont font = PdfFontFactory.createFont(loadEmployeesWithClassPathResource().getURI().toString());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
            pdfDoc.addNewPage();
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdfDoc);

            List<Reason> reasonList = new ArrayList<>();
            if (idContract.size() == 0) {
                reasonList = reasonRepository.findAllByTypeRecord("contract");
            } else {
                reasonList = reasonRepository.findAllByTypeRecordAndIdIn("contract", idContract);
            }

            int i = 0;
            for (Reason reason : reasonList) {
                Table table = new Table(new float[]{100, 400});
                table.addCell(new Cell().add(new Paragraph("Параметр").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(new Cell().add(new Paragraph("Значение").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                table.addCell(createCall(font, "Дата"));
                String pattern = "dd.MM.yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                table.addCell(createCall(font, simpleDateFormat.format(reason.getDate())));
                table.addCell(createCall(font, "Номер"));
                table.addCell(createCall(font, reason.getNumber()));
                table.addCell(createCall(font, "Сумма"));
                table.addCell(createCall(font, String.valueOf(reason.getSum())));
                table.addCell(createCall(font, "Поставщик"));
                table.addCell(createCall(font, reason.getCounterparty().getName()));
                doc.add(table);
                doc.add(new Paragraph());
                Table tableMaterialValueOrg = new Table(new float[]{150, 350});
                tableMaterialValueOrg.addCell(new Cell().add(new Paragraph("Тип МЦ").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                tableMaterialValueOrg.addCell(new Cell().add(new Paragraph("Наименование МЦ").setFont(font)).setBackgroundColor(ColorConstants.GRAY));
                List<MaterialValueOrgHistory> materialValueOrgHistoryList = new ArrayList<>();
                materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByReasonId(reason.getId());
                for (MaterialValueOrgHistory materialValueOrgHistory : materialValueOrgHistoryList) {
                    tableMaterialValueOrg.addCell(createCall(font, materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getMaterialValueType().getName()));
                    tableMaterialValueOrg.addCell(createCall(font, materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameFirm()+" "+materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameModel()));
                }
                doc.add(tableMaterialValueOrg);

                i++;
                if (i < reasonList.size()) {
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
