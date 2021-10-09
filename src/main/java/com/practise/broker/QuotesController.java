package com.practise.broker;

import com.practise.broker.error.CustomError;
import com.practise.broker.model.Quote;
import com.practise.broker.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.Optional;


@Controller("/quotes")
public class QuotesController {

    private final InMemoryStore inMemoryStore;

    public QuotesController(final InMemoryStore inMemoryStore) {
        this.inMemoryStore = inMemoryStore;
    }

    @Get("/{symbol}")
    public HttpResponse getQuote(String symbol) {
        Optional<Quote> mayBeQuote = inMemoryStore.fetchQuote(symbol);
        if (mayBeQuote.isEmpty()) {
            final CustomError notFound =  CustomError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("Quote not found for the symbol provided")
                    .path("/quotes/"+symbol)
                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(mayBeQuote.get());
    }
}
