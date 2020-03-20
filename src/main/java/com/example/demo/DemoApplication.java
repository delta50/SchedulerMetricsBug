package com.example.demo;

import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;


@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
        Schedulers.enableMetrics();
        Scheduler s = Schedulers.newParallel("test");
        s.schedule(() -> {assert(true);});
	}
    private static final String prometheusMetricPrefix = "actuator_";

    @Bean
    MeterRegistryCustomizer<PrometheusMeterRegistry> prometheusMetrics() {
        return registry -> registry.config()
                .namingConvention((NamingConvention) (name, type, baseUnit) -> prometheusMetricPrefix + Arrays.stream(name.split("\\."))
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("_")));
    }

}
