package finki.ikt.iktproekt.service;

import finki.ikt.iktproekt.model.Document;

import java.util.List;
import java.util.Optional;

public interface DocumentService {
    List<Document> findAll();
    Optional<Document> findById(Long id);
    Document create(Document document);
    Document update(Long id, Document document);
    void delete(Long id);
}
