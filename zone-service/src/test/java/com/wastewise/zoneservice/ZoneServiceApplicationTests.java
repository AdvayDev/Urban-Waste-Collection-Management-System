package com.wastewise.zoneservice;

import com.wastewise.zoneservice.controller.ZoneController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ZoneServiceApplicationTests {

    @Autowired
    private ZoneController zoneController;

    @Test
    void contextLoads() {
        assertThat(zoneController).isNotNull();
    }
}