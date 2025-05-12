package finki.ikt.iktproekt.document.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocumentDto {

    private Long id;

    private String filePath;

    private boolean processed;
}
