package bupt.goodservice.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceRequestStatus {
    COMPLETED(1),
    PUBLISHED(0),
    CANCELLED(-1);

    private final int value;

    ServiceRequestStatus(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static ServiceRequestStatus fromValue(int value) {
        for (ServiceRequestStatus status : ServiceRequestStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value);
    }
}
