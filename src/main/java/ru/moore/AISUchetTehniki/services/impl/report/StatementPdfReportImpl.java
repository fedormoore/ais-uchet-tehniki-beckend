package ru.moore.AISUchetTehniki.services.impl.report;

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
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.models.Entity.AccountSetting;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrgHistory;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.repositories.AccountSettingRepository;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgHistoryRepository;
import ru.moore.AISUchetTehniki.repositories.ReasonRepository;
import ru.moore.AISUchetTehniki.services.report.StatementPdfReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementPdfReportImpl implements StatementPdfReport {

    private final AccountSettingRepository accountSettingRepository;
    private final ReasonRepository reasonRepository;
    private final MaterialValueOrgHistoryRepository materialValueOrgHistoryRepository;

    private Resource loadEmployeesWithClassPathResource() {
        return new ClassPathResource("fonts/arial.ttf");
    }

    @Override
    public ByteArrayInputStream createReport(List<UUID> idStatement) {
        try {
            PdfFont font = PdfFontFactory.createFont(loadEmployeesWithClassPathResource().getURI().toString());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
            pdfDoc.addNewPage();
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdfDoc);

            List<Reason> reasonList = new ArrayList<>();
            if (idStatement.size() == 0) {
                reasonList = reasonRepository.findAllByTypeRecord("statement");
            } else {
                reasonList = reasonRepository.findAllByTypeRecordAndIdIn("statement", idStatement);
            }

            for (Reason reason : reasonList) {
                AccountSetting accountSetting = new AccountSetting();
                if (accountSettingRepository.findAll().size() > 0) {
                    accountSetting = accountSettingRepository.findAll().get(0);
                }
                if (accountSetting != null) {
                    Paragraph preambleParagraph = new Paragraph();
                    preambleParagraph.setFont(font);
                    preambleParagraph.add(accountSetting.getPreambleStatementReport());
                    preambleParagraph.setFontSize(12);
                    preambleParagraph.setTextAlignment(TextAlignment.RIGHT);
                    doc.add(preambleParagraph);
                    for (int j = 0; j < 10; j++) {
                        doc.add(new Paragraph());
                    }
                }


                Paragraph statementParagraph = new Paragraph();
                statementParagraph.setFont(font);
                statementParagraph.add("Заявление");
                statementParagraph.setFontSize(16);
                statementParagraph.setTextAlignment(TextAlignment.CENTER);
                doc.add(statementParagraph);
                doc.add(new Paragraph());

                Paragraph textParagraph = new Paragraph();
                textParagraph.setFont(font);
                textParagraph.add("Прошу списать с моего подотчета следующие материальные ценности:");
                textParagraph.setFontSize(12);
                textParagraph.setTextAlignment(TextAlignment.LEFT);
                doc.add(textParagraph);
                doc.add(new Paragraph());

                List<String> typeList = new ArrayList<>();
                typeList.add(HistoryTypeEnum.ADD_DEVICE.name());
                typeList.add(HistoryTypeEnum.REPLACEMENT.name());
                List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByReasonIdAndTypeInOrderByTypeAsc(reason.getId(), typeList);
                String type = "";
                int i = 1;
                for (MaterialValueOrgHistory materialValueOrgHistory : materialValueOrgHistoryList) {
                    Table table = new Table(1);
                    if (!type.equals(materialValueOrgHistory.getType())) {
                        if (!type.equals("")) {
                            doc.add(new Paragraph());
                        }
                        Paragraph locationParagraph = new Paragraph();
                        locationParagraph.setFont(font);
                        if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.ADD_DEVICE.getName())) {
                            locationParagraph.add("В связи с добавлением:");
                        }
                        if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.REPLACEMENT.getName())) {
                            locationParagraph.add("В связи с заменой:");
                        }
                        locationParagraph.setFontSize(12);
                        locationParagraph.setTextAlignment(TextAlignment.LEFT);
                        doc.add(locationParagraph);
                        doc.add(new Paragraph());

                        if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.ADD_DEVICE.getName())) {
                            table = new Table(new float[]{20, 240, 20, 240, 100});
                            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("№ п/п").setFont(font)).setTextAlignment(TextAlignment.CENTER));
                            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Списать МЦ").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Шт.").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                            table.addHeaderCell(new Cell(1, 2).add(new Paragraph("Включить в материальну ценность").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                            table.addHeaderCell(new Cell().add(new Paragraph("Наименование МЦ").setFont(font)).setTextAlignment(TextAlignment.CENTER));
                            table.addHeaderCell(new Cell().add(new Paragraph("Инв. №").setFont(font)).setTextAlignment(TextAlignment.CENTER));
                        }

                        if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.REPLACEMENT.getName())) {
                            table = new Table(new float[]{20, 160, 20, 160, 160, 100});
                            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("№ п/п").setFont(font)).setTextAlignment(TextAlignment.CENTER));
                            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Списать МЦ").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                            table.addHeaderCell(new Cell(2, 1).add(new Paragraph("Шт.").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));

                            table.addHeaderCell(new Cell(1, 3).add(new Paragraph("Включить в материальну ценность").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));

                            table.addHeaderCell(new Cell().add(new Paragraph("Наименование МЦ").setFont(font)).setTextAlignment(TextAlignment.CENTER));
                            table.addHeaderCell(new Cell().add(new Paragraph("Вывести из состава МЦ").setFont(font)).setTextAlignment(TextAlignment.CENTER));
                            table.addHeaderCell(new Cell().add(new Paragraph("Инв. №").setFont(font)).setTextAlignment(TextAlignment.CENTER));
                        }
                    }

                    if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.ADD_DEVICE.getName())) {
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(i)).setFont(font)).setTextAlignment(TextAlignment.CENTER));
                        String nameMaterialValueOrg = "";
                        if (materialValueOrgHistory.getChildren().get(0).getMaterialValueOrg().getMaterialValue().getNameInOrg() != null) {
                            nameMaterialValueOrg = materialValueOrgHistory.getChildren().get(0).getMaterialValueOrg().getMaterialValue().getNameInOrg();
                        }
                        if (materialValueOrgHistory.getChildren().get(0).getMaterialValueOrg().getMaterialValue().getNameFirm() != null) {
                            if (nameMaterialValueOrg != "") {
                                nameMaterialValueOrg = nameMaterialValueOrg + " " + materialValueOrgHistory.getChildren().get(0).getMaterialValueOrg().getChildren().get(0).getMaterialValue().getNameFirm() + " " + materialValueOrgHistory.getChildren().get(0).getMaterialValueOrg().getMaterialValue().getNameModel();
                            } else {
                                nameMaterialValueOrg = materialValueOrgHistory.getChildren().get(0).getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistory.getChildren().get(0).getMaterialValueOrg().getMaterialValue().getNameModel();
                            }
                        }
                        table.addCell(createCall(font, nameMaterialValueOrg));
                        table.addCell(new Cell().add(new Paragraph("1").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));

                        nameMaterialValueOrg = "";
                        if (materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameInOrg() != null) {
                            nameMaterialValueOrg = materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameInOrg();
                        }
                        if (materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameFirm() != null) {
                            if (nameMaterialValueOrg != "") {
                                nameMaterialValueOrg = nameMaterialValueOrg + " " + materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameModel();
                            } else {
                                nameMaterialValueOrg = materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameModel();
                            }
                        }
                        table.addCell(createCall(font, nameMaterialValueOrg));
                        String invNumber = "";
                        if (materialValueOrgHistory.getMaterialValueOrg().getInvNumber() != null) {
                            invNumber = materialValueOrgHistory.getMaterialValueOrg().getInvNumber();
                        }
                        table.addCell(createCall(font, invNumber));
                    }

                    if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.REPLACEMENT.getName())) {
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(i)).setFont(font)).setTextAlignment(TextAlignment.CENTER));

                        String nameMaterialValueOrg = "";
                        for (MaterialValueOrgHistory materialValueOrgHistoryIn: materialValueOrgHistory.getChildren()) {
                            if (materialValueOrgHistoryIn.getType().equals(HistoryTypeEnum.REPLACEMENT_IN.getName())){
                                if (materialValueOrgHistoryIn.getMaterialValueOrg().getMaterialValue().getNameInOrg() != null) {
                                    nameMaterialValueOrg = materialValueOrgHistoryIn.getMaterialValueOrg().getMaterialValue().getNameInOrg();
                                }
                                if (materialValueOrgHistoryIn.getMaterialValueOrg().getMaterialValue().getNameFirm() != null) {
                                    if (nameMaterialValueOrg != "") {
                                        nameMaterialValueOrg = nameMaterialValueOrg + " " + materialValueOrgHistoryIn.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistoryIn.getMaterialValueOrg().getMaterialValue().getNameModel();
                                    } else {
                                        nameMaterialValueOrg = materialValueOrgHistoryIn.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistoryIn.getMaterialValueOrg().getMaterialValue().getNameModel();
                                    }
                                }
                                table.addCell(createCall(font, nameMaterialValueOrg));
                            }
                        }


                        table.addCell(new Cell().add(new Paragraph("1").setFont(font)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));

                        nameMaterialValueOrg = "";
                        if (materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameInOrg() != null) {
                            nameMaterialValueOrg = materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameInOrg();
                        }
                        if (materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameFirm() != null) {
                            if (nameMaterialValueOrg != "") {
                                nameMaterialValueOrg = nameMaterialValueOrg + " " + materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameModel();
                            } else {
                                nameMaterialValueOrg = materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistory.getMaterialValueOrg().getMaterialValue().getNameModel();
                            }
                        }
                        table.addCell(createCall(font, nameMaterialValueOrg));

                        for (MaterialValueOrgHistory materialValueOrgHistoryTo: materialValueOrgHistory.getChildren()) {
                            if (materialValueOrgHistoryTo.getType().equals(HistoryTypeEnum.REPLACEMENT_TO.getName())) {
                                nameMaterialValueOrg = "";
                                if (materialValueOrgHistoryTo.getMaterialValueOrg().getMaterialValue().getNameInOrg() != null) {
                                    nameMaterialValueOrg = materialValueOrgHistoryTo.getMaterialValueOrg().getMaterialValue().getNameInOrg();
                                }
                                if (materialValueOrgHistoryTo.getMaterialValueOrg().getMaterialValue().getNameFirm() != null) {
                                    if (nameMaterialValueOrg != "") {
                                        nameMaterialValueOrg = nameMaterialValueOrg + " " + materialValueOrgHistoryTo.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistoryTo.getMaterialValueOrg().getMaterialValue().getNameModel();
                                    } else {
                                        nameMaterialValueOrg = materialValueOrgHistoryTo.getMaterialValueOrg().getMaterialValue().getNameFirm() + " " + materialValueOrgHistoryTo.getMaterialValueOrg().getMaterialValue().getNameModel();
                                    }
                                }
                                table.addCell(createCall(font, nameMaterialValueOrg));
                                String invNumber = "";
                                if (materialValueOrgHistoryTo.getMaterialValueOrg().getInvNumber() != null) {
                                    invNumber = materialValueOrgHistoryTo.getMaterialValueOrg().getInvNumber();
                                }
                                table.addCell(createCall(font, invNumber));
                            }
                        }

                    }
                    i++;
                    doc.add(table);
                }


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
