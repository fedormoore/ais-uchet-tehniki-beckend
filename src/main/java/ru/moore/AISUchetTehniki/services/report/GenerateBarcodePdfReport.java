package ru.moore.AISUchetTehniki.services.report;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface GenerateBarcodePdfReport {
    ByteArrayInputStream createReport(int size, List<String> idList);
}
