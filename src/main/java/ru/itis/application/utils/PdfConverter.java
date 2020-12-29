package ru.itis.application.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import ru.itis.application.CourseDto;
import ru.itis.application.models.Account;
import ru.itis.application.models.Course;
import ru.itis.application.services.AccountService;
import ru.itis.application.services.CourseService;
import ru.itis.application.services.impl.AccountServiceImpl;
import ru.itis.application.services.impl.CourseServiceImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PdfConverter {

    private final static String FILES_PATH = "C:\\java2021\\JavaLab\\rabbitmq_document_circulation\\files\\";
    private final static String PASS_TEMPLATE_PATH = "C:\\java2021\\JavaLab\\rabbitmq_document_circulation\\templates\\pass.pdf";
    private final static String CERTIFICATE_TEMPLATE_PATH = "C:\\java2021\\JavaLab\\rabbitmq_document_circulation\\templates\\certificate.pdf";

    private static CourseService courseService = new CourseServiceImpl();
    private static AccountService accountService = new AccountServiceImpl();

    public static void fillInfoInPass(Account account) {
        File file = new File(PASS_TEMPLATE_PATH);

        PDDocument document;
        try {
            document = PDDocument.load(file);
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            Map<String, String> data = new HashMap<>();
            data.put("fullName", account.getSurname() + " " + account.getName());
            data.put("email", account.getEmail());
            data.put("date", getDate(LocalDateTime.now().plusDays(20)));

            for (Map.Entry<String, String> item : data.entrySet()) {
                PDField field = acroForm.getField(item.getKey());
                if (field != null) {
                    if (field instanceof PDTextField) {
                        field.setValue(item.getValue());
                    }
                } else {
                    System.err.println("No field found with name: " + item.getKey());
                }
            }

            document.save(FILES_PATH + "PASS_" + account.getName() + "_" + account.getSurname() + ".pdf");
            document.close();

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        System.out.println("Insertion done");
    }

    public static void fillInfoInCertificate(CourseDto courseDto) {
        File file = new File(CERTIFICATE_TEMPLATE_PATH);

        PDDocument document;
        try {
            document = PDDocument.load(file);
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            Account account = accountService.getAccountByEmail(courseDto.getUserEmail());
            Course course = courseService.getCourseByUserIdAndCourseName(account.getId(), courseDto.getCourseName());

            Map<String, String> data = new HashMap<>();
            data.put("fullName", account.getSurname() + " " + account.getName());
            data.put("email", account.getEmail());
            data.put("date", getDate(course.getDate()));
            data.put("courseName", course.getCourseName());

            for (Map.Entry<String, String> item : data.entrySet()) {
                PDField field = acroForm.getField(item.getKey());
                if (field != null) {
                    if (field instanceof PDTextField) {
                        field.setValue(item.getValue());
                    }
                } else {
                    System.err.println("No field found with name: " + item.getKey());
                }
            }

            document.save(FILES_PATH + "CERTIFICATE_" + course.getCourseName() + "_" + account.getName() +
                    "_" + account.getSurname() + ".pdf");
            document.close();

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        System.out.println("Insertion done");
    }

    private static String getDate(LocalDateTime date) {
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear();
    }
}
