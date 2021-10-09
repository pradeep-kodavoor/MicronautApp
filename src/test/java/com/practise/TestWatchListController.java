package com.practise;

import com.practise.broker.WatchListController;
import com.practise.broker.error.CustomError;
import com.practise.broker.model.Quote;
import com.practise.broker.model.Symbol;
import com.practise.broker.model.WatchList;
import com.practise.broker.store.InMemoryAccountStore;
import com.practise.broker.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class TestWatchListController {

    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/account/watchlist")
    RxHttpClient client;

    @Inject
    InMemoryAccountStore store;

    private static final Logger LOG = LoggerFactory.getLogger(TestWatchListController.class);
    private final ThreadLocalRandom current = ThreadLocalRandom.current();

    @Test
    public void testReturnEmptyWatchList() {
        final WatchList result = client.toBlocking().retrieve("/", WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    public void testGetWatchList() {
        WatchList watchList = new WatchList();
        List<Symbol> symbols = Stream.of("APPL", "AMZN", "GOOG", "TSLA")
                .map(Symbol::new)
                .collect(Collectors.toList());
        watchList.setSymbols(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        final WatchList result = client.toBlocking().retrieve("/", WatchList.class);
        assertEquals(4, result.getSymbols().size());
        assertEquals(4, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    public void testUpdateWatchList() {
        WatchList watchList = new WatchList();
        List<Symbol> symbols = Stream.of("APPL", "AMZN", "GOOG", "TSLA")
                .map(Symbol::new)
                .collect(Collectors.toList());
        watchList.setSymbols(symbols);
        final HttpResponse<Object> result = client.toBlocking().exchange(PUT("/", watchList));
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    public void testDeleteWatchList() {
        WatchList watchList = new WatchList();
        List<Symbol> symbols = Stream.of("APPL", "AMZN", "GOOG", "TSLA")
                .map(Symbol::new)
                .collect(Collectors.toList());
        watchList.setSymbols(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
        final HttpResponse<Object> result = client.toBlocking().exchange(DELETE("/"+TEST_ACCOUNT_ID));
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }
}
