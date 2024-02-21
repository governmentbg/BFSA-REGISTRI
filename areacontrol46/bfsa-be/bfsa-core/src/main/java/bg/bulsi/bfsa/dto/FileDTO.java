package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.File;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FileDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("docTypeCode")
    private String docTypeCode;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("filePath")
    private String filePath;
    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("mimeType")
    private String mimeType;
    @JsonProperty("resource")
    private byte[] resource;

    public static FileDTO of(final File source) {
        FileDTO dto = new FileDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getDocType() != null) {
            dto.setDocTypeCode(source.getDocType().getCode());
        }
        return dto;
    }

    public static List<FileDTO> of(final List<File> files) {
        return files.stream().map(e -> of(e)).collect(Collectors.toList());
    }
}
