package com.practise.broker;

import com.practise.broker.model.Symbol;
import com.practise.broker.store.InMemoryStore;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;

@Controller("/markets")
public class MarketsController {

    private final InMemoryStore store;

    public MarketsController(final InMemoryStore store) {
        this.store = store;
    }

    @Get("/")
    public List<Symbol> all() {
        return store.getALlSymbols();
    }
}
