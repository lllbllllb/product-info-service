package com.lllbllllb.productinfoservice.core.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Build {

    @XmlAttribute
    private Double number;

    @XmlAttribute
    private String version;

    @XmlAttribute
    private String releaseDate;

    @XmlAttribute
    private String fullNumber;

    private List<Button> button;
}
