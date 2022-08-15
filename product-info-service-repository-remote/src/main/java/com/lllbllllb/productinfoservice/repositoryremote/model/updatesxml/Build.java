package com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.lllbllllb.productinfoservice.repositoryremote.jaxb.LocalDateJaxbAdapter;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Build {

    @XmlAttribute
    private String number;

    @XmlAttribute
    private String version;

    @XmlAttribute
    @XmlJavaTypeAdapter(LocalDateJaxbAdapter.class)
    private LocalDate releaseDate;

    @XmlAttribute
    private String fullNumber;

    private List<Button> button;
}
