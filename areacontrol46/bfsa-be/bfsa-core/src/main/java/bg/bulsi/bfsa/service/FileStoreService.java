package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FileDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.exception.FileStorageException;
import bg.bulsi.bfsa.model.File;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.FileStoreRepository;
import bg.bulsi.bfsa.repository.InspectionRepository;
import bg.bulsi.bfsa.repository.RecordRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FileStoreService {

    private final FileStoreRepository repository;
    private final NomenclatureService nomenclatureService;
    private final InspectionRepository inspectionRepository;
    private final RecordRepository recordRepository;

    @Value("${bfsa.file-store.base-path}")
    private String basePath;
    @Value("${bfsa.file-store.single-backup}")
    private boolean singleBackup;

    public File findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(File.class, id));
    }

    public File findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<File> findAllByRecordId(final Long recordId) {
        return repository.findAllByRecord_Id(recordId);
    }

    public List<File> findAllByInspectionId(final Long inspectionId) {
        return repository.findAllByInspection_Id(inspectionId);
    }

    public ResponseEntity<?> getFile(final Long id) {
        File file = findById(id);

        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(Paths.get(file.getFilePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
        headers.add("File-Name", file.getFileName());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileBytes.length)
                .contentType(file.getContentType() != null ? MediaType.valueOf(file.getContentType()) : null)
                .body(fileBytes);
    }

    public FileDTO get(final Long id) {
        File file = findById(id);
        FileDTO dto = FileDTO.of(file);

        try {
            dto.setResource(Files.readAllBytes(Paths.get(file.getFilePath())));
        } catch (IOException e) {
            log.error("Error getting file with id: " + id, e);
        }
        return dto;
    }

//    @Transactional
//    public File create(final String docTypeCode, final String fileName, final String contentType, final InputStream fis) {
//        File file = buildFile(docTypeCode, fileName, contentType, null, null);
//
//        file.setFilePath(
//                basePath + java.io.File.separator + file.getDocType().getCode() +
//                        java.io.File.separator + System.currentTimeMillis() + "_" + UUID.randomUUID() + "." +
//                        FilenameUtils.getExtension(fileName)
//        );
//
//        return update(file, fis);
//    }

    @Transactional
    public File create(final String docTypeCode, final MultipartFile multipartFile) {
        File file = buildFile(docTypeCode, multipartFile);

        file.setFilePath(
                basePath + java.io.File.separator + file.getDocType().getCode() +
                        java.io.File.separator + System.currentTimeMillis() + "_" + UUID.randomUUID() + "." +
                        FilenameUtils.getExtension(multipartFile.getOriginalFilename())
        );

        return update(file, multipartFile);
    }

    @Transactional
    public File create(final String docTypeCode, final Long inspectionId, final MultipartFile multipartFile) {

        Inspection inspection = inspectionRepository.findById(inspectionId).orElseThrow(() -> new EntityNotFoundException(Record.class, inspectionId));

        File file = buildFile(docTypeCode, multipartFile, inspection);

        file.setFilePath(
                basePath + java.io.File.separator + "INSPECTION" + inspection.getId() +
                        java.io.File.separator + System.currentTimeMillis() + "_" + UUID.randomUUID() + "." +
                        FilenameUtils.getExtension(multipartFile.getOriginalFilename())
        );

        return update(file, multipartFile);
    }

    @Transactional
    public File create(final String docTypeCode, final Long recordId, final String fileName, final String contentType,
                       final byte[] fileContent) throws IOException {

        Record record = recordRepository.findById(recordId).orElseThrow(() -> new EntityNotFoundException(Record.class, recordId));

        File file = buildFile(docTypeCode, fileName, contentType, record, null);

        file.setFilePath(
                basePath + java.io.File.separator + "RECORD" + record.getId() +
                        java.io.File.separator + System.currentTimeMillis() + "_" + UUID.randomUUID() + "." +
                        FilenameUtils.getExtension(fileName)
        );

        return update(file, new ByteArrayInputStream(fileContent));
    }

    private File buildFile(final String docTypeCode, final MultipartFile multipartFile) {
        return buildFile(docTypeCode, multipartFile.getOriginalFilename(), multipartFile.getContentType(), null, null);
    }

    private File buildFile(final String docTypeCode, final MultipartFile multipartFile, Record record) {
        return buildFile(docTypeCode, multipartFile.getOriginalFilename(), multipartFile.getContentType(), record, null);
    }

    private File buildFile(final String docTypeCode, final MultipartFile multipartFile, Inspection inspection) {
        return buildFile(docTypeCode, multipartFile.getOriginalFilename(), multipartFile.getContentType(), null, inspection);
    }

    private File buildFile(final String docTypeCode, final String fileName, final String contentType, final Record record, final Inspection inspection) {
        File file = File.builder()
                .fileName(fileName)
                .record(record)
                .inspection(inspection)
                .docType(nomenclatureService.findByCode(docTypeCode))
                .contentType(contentType).build();

        if (file.getDocType() == null) {
            throw new RuntimeException("DocType is mandatory");
        }
        return file;
    }

    @Transactional
    public File update(final Long id, final MultipartFile file) {
        return update(findById(id), file);
    }

    private File update(final File file, final MultipartFile multipartFile) {
        if (file == null) {
            throw new RuntimeException("Can't update FileStore null");
        }
        if (multipartFile == null) {
            throw new RuntimeException("Can't update FileStore multipartFile is null");
        }
        if (file.getDocType() == null) {
            throw new RuntimeException("Doc type is empty");
        }
        if (!StringUtils.hasText(file.getFilePath())) {
            throw new RuntimeException("File path is empty");
        }
        try {
            repository.save(file);
            store(file, multipartFile);
        } catch (Exception e) {
            log.error("Could not store file: {}. Please try again.", file.getFilePath(), e);
            throw new FileStorageException("Could not store file " + file.getFileName()
                    + ". Please try again.", e);
        }
        return file;
    }

    private File update(final File file, final InputStream fis) {
        if (file == null) {
            throw new RuntimeException("Can't update FileStore file is null");
        }
        if (fis == null) {
            throw new RuntimeException("Can't update FileStore fis is null");
        }
        if (file.getDocType() == null) {
            throw new RuntimeException("Doc type is empty");
        }
        if (!StringUtils.hasText(file.getFilePath())) {
            throw new RuntimeException("File path is empty");
        }
        try {
            repository.save(file);
            store(file, fis);
        } catch (Exception e) {
            log.error("Could not store file: {}. Please try again.", file.getFilePath(), e);
            throw new FileStorageException("Could not store file " + file.getFileName()
                    + ". Please try again.", e);
        }
        return file;
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    private void store(final File file, final MultipartFile multipartFile) throws IOException {
        store(file, multipartFile.getInputStream());
    }

    private void store(final File file, final InputStream fis) throws IOException {
        Files.createDirectories(
                Paths.get(file.getFilePath().substring(0, ordinalIndexOf(file.getFilePath(), java.io.File.separator, 5)))
        );
        if (singleBackup && Files.exists(Paths.get(file.getFilePath()))) {
            Files.move(Paths.get(file.getFilePath()), Paths.get(file.getFilePath() + ".back"), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.copy(fis, Paths.get(file.getFilePath()), StandardCopyOption.REPLACE_EXISTING);
    }

    @Transactional
    public void delete(final Long id) {
        delete(findById(id));
    }

    private void delete(final File file) {
        if (!StringUtils.hasText(file.getFilePath())) {
            throw new RuntimeException("File path is empty");
        }
        try {
            if (!StringUtils.hasText(file.getFilePath())) {
                throw new RuntimeException("File path is empty");
            }
            repository.delete(file);
            Files.delete(Paths.get(file.getFilePath()));
        } catch (IOException e) {
            log.error("Could not delete file: {}.", file.getFilePath(), e);
            throw new FileStorageException("Could not delete file: " + file.getFileName()
                    + ". Please try again.", e);
        }
    }

}