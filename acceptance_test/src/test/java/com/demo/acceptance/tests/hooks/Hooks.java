package com.demo.acceptance.tests.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import com.demo.acceptance.tests.repository.TestDataRepository;
import io.cucumber.java.After;

/**
 * Cucumber hook.
 */
public class Hooks {

    @Autowired
    private TestDataRepository testDataRepository;

    @After
    public void afterAnyTest() {
        testDataRepository.resetTestDataRepository();
    }
}
