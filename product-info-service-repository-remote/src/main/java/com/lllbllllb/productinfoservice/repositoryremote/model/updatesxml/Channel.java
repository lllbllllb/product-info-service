package com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Channel {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String status;

    private List<Build> build;

    @XmlAttribute
    private String url;
}
