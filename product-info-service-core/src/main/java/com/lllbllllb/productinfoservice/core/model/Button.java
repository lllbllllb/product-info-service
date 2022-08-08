package com.lllbllllb.productinfoservice.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Button {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String url;

    @XmlAttribute
    private boolean download;
}
