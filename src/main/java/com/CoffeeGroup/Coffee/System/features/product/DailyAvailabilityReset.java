package com.CoffeeGroup.Coffee.System.features.product;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyAvailabilityReset {

    private final CafeProductRepository cafeProductRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "${app.timezone}")
    @Transactional
    public void makeEverythingAvailableAgain() {
        int reset = cafeProductRepository.markEverythingAvailable();
        log.info("Reinicio diario: {} productos marcados como disponibles otra vez", reset);
    }
}
