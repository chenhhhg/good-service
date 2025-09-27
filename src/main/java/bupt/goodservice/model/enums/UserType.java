package bupt.goodservice.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    USER(0),
    ADMIN(1);

    private final int value;

    UserType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static UserType fromValue(int value) {
        for (UserType type : UserType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value);
    }
}
