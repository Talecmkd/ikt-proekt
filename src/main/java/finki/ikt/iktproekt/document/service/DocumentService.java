package finki.ikt.iktproekt.document.service;

import finki.ikt.iktproekt.document.model.Document;
import finki.ikt.iktproekt.user.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentService {

    List<Document> findAll();

    Optional<Document> findById(Long id);

    Document create(Document document);

    Document update(Long id, Document document);

    void delete(Long id);

    Document validateAndSaveFile(MultipartFile file, User user);
}
