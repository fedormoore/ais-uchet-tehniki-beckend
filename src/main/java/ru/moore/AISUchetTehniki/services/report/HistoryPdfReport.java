package ru.moore.AISUchetTehniki.services.report;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface HistoryPdfReport {
    ByteArrayInputStream createReport(List<String> idList);
}
