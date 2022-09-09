package io.citytrees.configuration.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

import java.sql.JDBCType;

@WritingConverter
public class WritingEnumConverter<E extends Enum<E>> implements Converter<E, JdbcValue> {

    @Override
    public JdbcValue convert(E source) {
        return JdbcValue.of(source.name(), JDBCType.OTHER);
    }
}
