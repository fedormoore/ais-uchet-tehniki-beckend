package ru.moore.AISUchetTehniki.services.report;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface StatusCartridgePdfReport {
    ByteArrayInputStream createReport(List<String> status);
}
