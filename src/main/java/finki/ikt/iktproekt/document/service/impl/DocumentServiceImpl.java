package finki.ikt.iktproekt.document.service.impl;

import finki.ikt.iktproekt.document.model.Document;
import finki.ikt.iktproekt.document.repository.DocumentRepository;
import finki.ikt.iktproekt.document.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

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
//            document.setDocument_id(id);
            return documentRepository.save(document);
        }
        throw new RuntimeException("Document not found");
    }

    @Override
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }
}
