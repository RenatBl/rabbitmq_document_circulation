package ru.itis.application.utils;

import ru.itis.application.CourseDto;

public class CertificateCreator {

    public static void createCertificate(CourseDto courseDto) {
        PdfConverter.fillInfoInCertificate(courseDto);
    }
}
