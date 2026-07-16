package com.hps.pfa.incident_investigator.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RagTestController {

    private final VectorStore vectorStore;

    public RagTestController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @GetMapping("/api/rag-search")
    public String search(@RequestParam String query) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build()
        );
        return results.stream()
                .map(d -> "Source: " + d.getMetadata().get("source") + "\n" + d.getText())
                .collect(Collectors.joining("\n---\n"));
    }
}