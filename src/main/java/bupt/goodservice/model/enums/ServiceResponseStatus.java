package bupt.goodservice.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceResponseStatus {
    PENDING(0),
    ACCEPTED(1),
    REJECTED(2),
    CANCELLED(3);

    private final int value;

    ServiceResponseStatus(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }
    
    public static ServiceResponseStatus fromValue(int value) {
        for (ServiceResponseStatus status : ServiceResponseStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value);
    }
}
