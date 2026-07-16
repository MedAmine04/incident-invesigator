package com.hps.pfa.incident_investigator.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ContractCorpusIngestionRunner implements CommandLineRunner {

    private final VectorStore vectorStore;

    public ContractCorpusIngestionRunner(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws IOException {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        List<Document> existing = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("contrat SLA")
                        .topK(1)
                        .filterExpression(b.eq("corpus", "contracts").build())
                        .build()
        );
        if (!existing.isEmpty()) {
            System.out.println(">>> Corpus contrats deja ingere, ingestion ignoree.");
            return;
        }

        System.out.println(">>> Ingestion du corpus de contrats clients...");

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:contracts-corpus/*.txt");

        List<Document> documents = new ArrayList<>();
        for (Resource resource : resources) {
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String filename = resource.getFilename();
            documents.add(new Document(content, Map.of("source", filename, "corpus", "contracts")));
        }

        System.out.println(">>> " + documents.size() + " contrats source charges.");

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        System.out.println(">>> " + chunks.size() + " chunks generes apres decoupage.");

        vectorStore.add(chunks);

        System.out.println(">>> Ingestion terminee : " + chunks.size() + " chunks de contrats vectorises.");
    }
}