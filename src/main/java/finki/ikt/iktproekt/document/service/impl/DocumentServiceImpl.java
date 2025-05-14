package finki.ikt.iktproekt.document.service.impl;

import finki.ikt.iktproekt.document.model.Document;
import finki.ikt.iktproekt.document.model.dto.DocumentDto;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.user.model.User;

import finki.ikt.iktproekt.document.repository.DocumentRepository;

import finki.ikt.iktproekt.document.service.DocumentService;

import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public Document create(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Document update(Long id, Document document) {
        if (documentRepository.existsById(id)) {
            document.setId(id);
            return documentRepository.save(document);
        }
        throw new RuntimeException("Document not found");
    }

    @Override
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    @Override
    public Document validateAndSaveFile(MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new IOException("File must be a PDF");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("File size must be less than 10MB");
        }

        try (PDDocument pdfDocument = PDDocument.load(file.getInputStream())) {
            if (pdfDocument.isEncrypted()) {
                throw new IOException("PDF is password-protected and cannot be processed");
            }
            if (pdfDocument.getNumberOfPages() == 0) {
                throw new IOException("PDF contains no pages");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdfDocument);

            if (text == null || text.trim().isEmpty()) {
                throw new IOException("PDF contains no readable text");
            }
            if (text.trim().length() < 50) {
                throw new IOException("PDF must contain at least 50 characters of text");
            }
        } catch (IOException e) {
            throw new IOException("Invalid PDF: " + e.getMessage(), e);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new IOException("Failed to save file: " + e.getMessage(), e);
        }

        Document document = new Document();
        document.setFilePath(filePath.toString());
        document.setUser(user);

        return create(document);
    }

    @Override
    public Document findDocumentByQuiz(Quiz quiz) {
        return documentRepository.findDocumentByQuiz(quiz);
    }

    @Override
    public DocumentDto mapDocumentToDto(Document document) {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setId(document.getId());
        documentDto.setFilePath(document.getFilePath());
        documentDto.setProcessed(document.isProcessed());

        return documentDto;
    }
}
