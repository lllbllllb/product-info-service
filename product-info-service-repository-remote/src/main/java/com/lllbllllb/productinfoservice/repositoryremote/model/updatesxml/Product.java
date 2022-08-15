package com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {

    @XmlAttribute
    private String name;

    private Set<String> code;

    private List<Channel> channel;
}
