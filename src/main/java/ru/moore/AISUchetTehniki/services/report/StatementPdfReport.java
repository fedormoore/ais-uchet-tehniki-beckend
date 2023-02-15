package ru.moore.AISUchetTehniki.services.report;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

public interface StatementPdfReport {
    ByteArrayInputStream createReport(List<UUID> idStatement);
}
