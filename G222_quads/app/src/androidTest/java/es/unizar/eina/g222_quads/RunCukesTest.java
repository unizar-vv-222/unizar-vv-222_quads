package es.unizar.eina.g222_quads;

import io.cucumber.junit.CucumberOptions;

@CucumberOptions(
        features = {"features"},
        glue = {"es.unizar.eina.g222_quads"}
)
public class RunCukesTest {
}