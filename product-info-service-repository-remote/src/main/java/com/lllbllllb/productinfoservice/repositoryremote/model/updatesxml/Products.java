package com.lllbllllb.productinfoservice.repositoryremote.model.updatesxml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "products")
public class Products {

    private List<Product> product;

}
