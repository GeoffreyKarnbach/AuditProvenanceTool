package project.backend.service.impl;

import project.backend.service.ExampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileReader;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {

    @Override
    public void example_method() {
        Model model = ModelFactory.createDefaultModel();
        try {
            FileReader reader = new FileReader("src/main/resources/ai_system_description.ttl");
            model.read(reader, null, "TTL");
        } catch (Exception e) {
            log.error("Error reading ontology file", e);
        }
        log.info("Ontology loaded successfully");
    }
}
