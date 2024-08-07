package org.guillaumechamp.discordbot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class MainTest {
    @Test
    void shouldMainWorkWithoutException(){
        assertThatNoException().isThrownBy(()->Main.main(null));
        Main.api.shutdown();
        Main.api = null;
    }
}
