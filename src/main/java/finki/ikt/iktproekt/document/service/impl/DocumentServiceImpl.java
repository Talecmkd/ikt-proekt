package finki.ikt.iktproekt.document.service.impl;

import finki.ikt.iktproekt.document.model.Document;
import finki.ikt.iktproekt.document.model.dto.DocumentDto;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.user.model.User;

import finki.ikt.iktproekt.document.repository.DocumentRepository;

import finki.ikt.iktproekt.document.service.DocumentService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    public Document validateAndSaveFile(MultipartFile file, User user) {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Wrong file type. File must be PDF.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("File size must be less than 10MB");
        }

        String filePath = uploadDir + File.separator + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
        } catch (IOException e) {
            throw new RuntimeException("File saving failed");
        }

        Document document = new Document();
        document.setFilePath(filePath);
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
