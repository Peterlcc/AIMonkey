package com.peter.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lcc
 * @date 2020/11/5 4:31
 */
@Component
@Slf4j
@Setter
@Getter
public class DateFormater implements Converter<Date,String> {

    private SimpleDateFormat simpleDateFormat=null;

    public DateFormater(@Value("${buaa.data-format-pattern}") String pattern) {
        simpleDateFormat=new SimpleDateFormat(pattern);
    }

    @Override
    public String convert(Date date) {
        String res = simpleDateFormat.format(date);
        return res;
    }
}
