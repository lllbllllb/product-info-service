package com.lllbllllb.productinfoservice.core.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "products")
public class Products {

    private List<Product> product;

}
