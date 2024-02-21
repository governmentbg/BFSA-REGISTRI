package bg.bulsi.bfsa.custom;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Getter
@Setter
public class CustomMultipartFile implements MultipartFile {

    private final File file;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    public CustomMultipartFile(File file, String contentType) {
        this.file = file;
        this.name = file.getName();
        this.originalFilename = file.getName();
        this.contentType = contentType;
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        FileCopyUtils.copy(file, dest);
    }
}
