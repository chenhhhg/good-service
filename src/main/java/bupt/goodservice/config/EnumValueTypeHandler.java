package bupt.goodservice.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EnumValueTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private Class<E> type;
    private final E[] enums;

    public EnumValueTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
        if (this.enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }

    private E fromValue(int value) {
        return Arrays.stream(enums)
                .filter(e -> {
                    try {
                        return (int) e.getClass().getMethod("getValue").invoke(e) == value;
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot convert " + value + " to " + type.getSimpleName()));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setInt(i, (int) parameter.getClass().getMethod("getValue").invoke(parameter));
        } catch (Exception e) {
            throw new SQLException("Error setting enum value", e);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return fromValue(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return fromValue(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return fromValue(value);
    }
}
