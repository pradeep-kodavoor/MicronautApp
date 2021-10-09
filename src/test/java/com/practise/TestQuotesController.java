package com.practise;

import com.practise.broker.error.CustomError;
import com.practise.broker.model.Quote;
import com.practise.broker.model.Symbol;
import com.practise.broker.store.InMemoryStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.text.html.Option;


import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class TestQuotesController {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    InMemoryStore store;

    private static final Logger LOG = LoggerFactory.getLogger(TestQuotesController.class);
    private final ThreadLocalRandom current = ThreadLocalRandom.current();

    private Quote initRandomQuote(String symbolValue) {
         return Quote.builder().symbol(new Symbol(symbolValue))
                .bid(randomValue())
                .ask(randomValue())
                .lastPrice(randomValue())
                .volume(randomValue())
                .build();
    }

    @Test
    public void testGetQuote() {
        final Quote apple = initRandomQuote("APPL");
        final Quote google = initRandomQuote("GOOG");
        store.update(apple);
        store.update(google);
        final Quote appleResult = client.toBlocking().retrieve(GET("/quotes/APPL"), Quote.class);
        final Quote googleResult = client.toBlocking().retrieve(GET("/quotes/GOOG"), Quote.class);
        assertEquals(store.fetchQuote("APPL").get(), appleResult);
        assertThat(apple).usingRecursiveComparison().isEqualTo(appleResult);
        assertThat(google).usingRecursiveComparison().isEqualTo(googleResult);
        // LOG.debug("Result: {}", appleResult);
    }

    @Test
    public void testQuoteNotFound() {
        try {
            final Quote appleResult = client.toBlocking().retrieve(GET("/quotes/UNSUPPORTED"), Quote.class);
        } catch(HttpClientResponseException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getResponse().getStatus());
            final Optional<CustomError> customErrorOptional = ex.getResponse().getBody(CustomError.class);
            assertTrue(customErrorOptional.isPresent());
            assertEquals(404, customErrorOptional.get().getStatus());
            assertEquals("NOT_FOUND", customErrorOptional.get().getError());
            assertEquals("Quote not found for the symbol provided", customErrorOptional.get().getMessage());
            assertEquals("/quotes/UNSUPPORTED", customErrorOptional.get().getPath());
        }
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(current.nextDouble(1,100));
    }
}
