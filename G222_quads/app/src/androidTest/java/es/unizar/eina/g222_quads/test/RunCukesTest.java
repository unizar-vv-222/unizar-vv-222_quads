package es.unizar.eina.g222_quads.test;

import io.cucumber.junit.CucumberOptions;

@CucumberOptions(
        features = {"features"},
        glue = {"es.unizar.eina.g222_quads.test"}
)
public class RunCukesTest {
}