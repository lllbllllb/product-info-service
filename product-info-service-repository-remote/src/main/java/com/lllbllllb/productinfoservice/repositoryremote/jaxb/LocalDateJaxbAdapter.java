package com.lllbllllb.productinfoservice.repositoryremote.jaxb;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public LocalDate unmarshal(String value) {
        return LocalDate.parse(value, FORMATTER);
    }

    @Override
    public String marshal(LocalDate localDate) {
        return localDate.format(FORMATTER);
    }
}
