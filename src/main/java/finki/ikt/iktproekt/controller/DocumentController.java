//package finki.ikt.iktproekt.controller;
//
//import finki.ikt.iktproekt.Service.DocumentService;
//import finki.ikt.iktproekt.Service.UserService;
//import finki.ikt.iktproekt.model.Document;
//import finki.ikt.iktproekt.model.User;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@RestController
//@RequestMapping("/api/documents")
//public class DocumentController {
//    private final DocumentService documentService;
//    private final UserService userService;
//
//    public DocumentController(DocumentService documentService, UserService userService) {
//        this.documentService = documentService;
//        this.userService = userService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
//        // Check if the file is PDF
//        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
//            return ResponseEntity.badRequest().body("Wrong file type. File must be PDF.");
//        }
//
//        // File size validation
//        if (file.getSize() > 10 * 1024 * 1024) {
//            return ResponseEntity.badRequest().body("File size must be less than 10MB");
//        }
//
//        // Saving the file locally
//        String filePath = "uploads/" + file.getOriginalFilename();
//        try {
//            Path path = Paths.get(filePath);
//            Files.createDirectories(path.getParent());
//            file.transferTo(path);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File saving failed");
//        }
//
//        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        Document document = new Document(null, user, filePath, false, null);
//
//        documentService.create(document);
//
//        return ResponseEntity.ok("File uploaded successfully");
//    }
//}
