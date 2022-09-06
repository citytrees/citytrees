package io.citytrees.configuration.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public abstract class AbstractReadingEnumConverter<E extends Enum<E>> implements Converter<String, E> {

    private final Class<E> enumClass;

    protected AbstractReadingEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E convert(String source) {
        return Enum.valueOf(enumClass, source);
    }
}
