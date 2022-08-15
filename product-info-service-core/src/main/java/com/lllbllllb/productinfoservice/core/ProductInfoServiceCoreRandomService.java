package com.lllbllllb.productinfoservice.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceCoreRandomService {

    public <T> List<T> shuffle(Collection<T> source) {
        var copy = new ArrayList<>(source);

        Collections.shuffle(copy);

        return List.copyOf(copy);
    }
}
