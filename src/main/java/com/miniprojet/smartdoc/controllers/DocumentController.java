package com.miniprojet.smartdoc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.miniprojet.smartdoc.services.RagService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final RagService ragService;
    private final String uploadDir = "uploads";

    public DocumentController(RagService ragService) {
        this.ragService = ragService;
    }

    private Path getUploadPath() throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("Dossier créé : " + uploadPath);
        }
        return uploadPath;
    }


    @PostMapping(value = "/upload/pdf", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Le fichier PDF est vide");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf"))
            return ResponseEntity.badRequest().body("Veuillez uploader un fichier PDF valide");

        try {
            Path uploadPath = getUploadPath();
            String safeFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            Path filePath = uploadPath.resolve(safeFilename);

            file.transferTo(filePath.toFile());
            System.out.println("Fichier sauvegardé : " + filePath.toAbsolutePath());

            // Extraction du texte pour RAG
            try (PDDocument document = PDDocument.load(filePath.toFile())) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                ragService.indexDocument(safeFilename, text); 
            }

            return ResponseEntity.ok("PDF sauvegardé et indexé pour RAG");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload ou de l'indexation : " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload/csv", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Le fichier CSV est vide");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv"))
            return ResponseEntity.badRequest().body("Veuillez uploader un fichier CSV valide");

        try {
            Path uploadPath = getUploadPath();
            String safeFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            Path filePath = uploadPath.resolve(safeFilename);

            file.transferTo(filePath.toFile());
            System.out.println("Fichier CSV sauvegardé : " + filePath.toAbsolutePath());

            String text = Files.readString(filePath, StandardCharsets.UTF_8);
            ragService.indexDocument(safeFilename, text);

            return ResponseEntity.ok("CSV sauvegardé et indexé pour RAG");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload ou de l'indexation : " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<String[]> listFiles() {
        try {
            Path uploadPath = getUploadPath();
            File folder = uploadPath.toFile();
            String[] files = folder.list();
            if (files == null) files = new String[]{};
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            Path uploadPath = getUploadPath();
            Path filePath = uploadPath.resolve(Paths.get(filename).getFileName().toString());

            if (!Files.exists(filePath)) return ResponseEntity.notFound().build();

            byte[] data = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filePath.getFileName() + "\"")
                    .body(data);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            Path uploadPath = getUploadPath();
            Path filePath = uploadPath.resolve(Paths.get(filename).getFileName().toString());

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Le fichier " + filename + " n'existe pas");
            }

            Files.delete(filePath);
            return ResponseEntity.ok("Fichier " + filename + " supprimé avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du fichier : " + e.getMessage());
        }
    }
}
