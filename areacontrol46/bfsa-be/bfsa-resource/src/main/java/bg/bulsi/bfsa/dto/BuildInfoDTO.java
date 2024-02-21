package bg.bulsi.bfsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildInfoDTO {

    private String group;
    private String artifact;
    private String name;
    private String version;
    private Instant time;
    private String gitCommitId;
    private String gitCommitTime;

    public static BuildInfoDTO of(final BuildProperties bp) {
        BuildInfoDTO dto = new BuildInfoDTO();
        BeanUtils.copyProperties(bp, dto);
        if (StringUtils.hasText(bp.get("git.commitId")) && !"N/A".equals(bp.get("git.commitId"))) {
            dto.setGitCommitId(bp.get("git.commitId"));
        }
        if (StringUtils.hasText(bp.get("git.commitTime")) && !"N/A".equals(bp.get("git.commitTime"))) {
            dto.setGitCommitTime(bp.get("git.commitTime"));
        }
        return dto;
    }
}
