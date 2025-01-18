package com.example.metadata.processing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for ProcessingApplication to ensure the Spring context loads correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessingApplication.class)
public class ProcessingApplicationTest {

  @Test
  public void contextLoads() {
    // If the application context loads without any issues, this test passes.
  }
}
