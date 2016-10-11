package com.example.application;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.riemann.Riemann;
import com.codahale.metrics.riemann.RiemannReporter;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class ExampleApplication extends Application<ExampleConfiguration>{

    public static void main(String[] args) throws Exception {
        new ExampleApplication().run(args);
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception {

        if(configuration.metricsEnabled()) {


            final Riemann riemann = new Riemann("riemann.example.com", 5555);

            final RiemannReporter reporter = RiemannReporter.forRegistry(environment.metrics())
                    .prefixedWith("prefix")
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(riemann);
            reporter.start(5, TimeUnit.SECONDS);

            final ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(environment.metrics()).build();
            consoleReporter.start(5, TimeUnit.SECONDS);
        }

        final ExampleResource exampleResource = new ExampleResource(environment.metrics());
        environment.jersey().register(exampleResource);
    }
}
