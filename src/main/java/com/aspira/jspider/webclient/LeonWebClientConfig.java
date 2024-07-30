package com.aspira.jspider.webclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LeonWebClientConfig {

    @Value("${web-client.base-url}")
    private String baseUrl;

    @Bean
    public WebClient leonWebClient() {
        final var exchangeStrategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
                .build();

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(baseUrl)
                .filter(withRetryableRequests())
                .build();
    }

    protected ExchangeFilterFunction withRetryableRequests() {
        return (request, next) -> next.exchange(request)
                .flatMap(response -> {
                    if (response.statusCode() == TOO_MANY_REQUESTS) {
                        return Mono.error(new WebClientResponseException("Too Many Requests",
                                response.statusCode().value(), response.statusCode().toString(),
                                response.headers().asHttpHeaders(), null, null));
                    }
                    return Mono.just(response);
                })
                .retryWhen(retryBackoffSpec());
    }

    private RetryBackoffSpec retryBackoffSpec() {
        return Retry.backoff(getMaxAttempts(), Duration.ofSeconds(1))
                .filter(this::isRetryableError)
                .doBeforeRetry(retrySignal ->
                        log.warn("Retrying request after exception: {}", retrySignal.failure().getLocalizedMessage()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new RuntimeException("Retry attempts exhausted"));
    }

    private long getMaxAttempts() {
        return 3;
    }

    private boolean isRetryableError(Throwable throwable) {
        return throwable instanceof WebClientResponseException &&
                (is4xxClientError(throwable) || ((WebClientResponseException) throwable).getStatusCode() == TOO_MANY_REQUESTS);
    }

    private boolean is4xxClientError(Throwable throwable) {
        return throwable instanceof WebClientResponseException &&
                ((WebClientResponseException) throwable).getStatusCode().is4xxClientError() &&
                ((WebClientResponseException) throwable).getStatusCode() != TOO_MANY_REQUESTS;
    }

}