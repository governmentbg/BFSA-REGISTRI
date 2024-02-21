package bg.bulsi.bfsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

	private String type;
	private String token;
	private String refreshToken;
	private Long userId;
	private Long branchId;
	private String directorateCode;
	private String email;
	private String username;
	private String fullName;
	private List<String> roles;

}
