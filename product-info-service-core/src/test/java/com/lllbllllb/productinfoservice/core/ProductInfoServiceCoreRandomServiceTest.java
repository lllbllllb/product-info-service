package com.lllbllllb.productinfoservice.core;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test for {@link ProductInfoServiceCoreRandomService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceCoreRandomServiceTest {

    @InjectMocks
    private ProductInfoServiceCoreRandomService service;

    @Test
    void shouldShuffleList() {
        // given
        var source = IntStream.range(0, 1000)
            .boxed()
            .collect(Collectors.toList());

        // when
        var shuffled = service.shuffle(source);

        // then
        assertAll(
            () -> assertNotEquals(source, shuffled),
            () -> assertThat(shuffled, containsInAnyOrder(source.toArray(new Integer[0])))
        );
    }

    @Test
    void shouldShuffleSet() {
        // given
        var source = IntStream.range(0, 1000)
            .boxed()
            .collect(Collectors.toSet());

        // when
        var shuffled = service.shuffle(source);

        // then
        assertThat(shuffled, containsInAnyOrder(source.toArray(new Integer[0])));
    }
}
