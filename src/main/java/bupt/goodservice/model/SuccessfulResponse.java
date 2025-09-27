package bupt.goodservice.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SuccessfulResponse {
    private Long id;
    private Long requestId;
    private Long requestUserId;
    private Long responseId;
    private Long responseUserId;
    private LocalDate acceptedDate;
}
