package bg.bulsi.bfsa.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Slf4j
@Configuration
public class HttpClientConfig {

//    @Bean
//    WebClient webClient(
//            @Value("${bfsa.e-payment.keystore}") String keystore,
//            @Value("${bfsa.e-payment.keystore-password}") String keyStorePassword,
//            @Value("${bfsa.e-payment.truststore}") String truststore,
//            @Value("${bfsa.e-payment.truststore-password}") String trustStorePassword,
//            @Value("${bfsa.e-payment.base-url}") String baseUrl
//    ) {
//        ClientHttpConnector httpConnector = new ReactorClientHttpConnector(
//                HttpClient.create().secure(t -> t.sslContext(
//                        sslContext(getKeyStore(keystore, keyStorePassword), getTrustStore(truststore, trustStorePassword))
//                ))
//        );
//        return WebClient.builder()
//                .clientConnector(httpConnector)
//                .baseUrl(baseUrl)
//                .filter(logRequest())
//                .filter(logResponse())
//                .build();
//    }

    @Bean
    public HttpClient httpClient(
            @Value("${bfsa.e-payment.keystore}") String keystore,
            @Value("${bfsa.e-payment.keystore-password}") String keyStorePassword,
            @Value("${bfsa.e-payment.truststore}") String truststore,
            @Value("${bfsa.e-payment.truststore-password}") String trustStorePassword,
            @Value("${bfsa.e-payment.base-url}") String baseUrl) throws Exception {
        // Load the keystore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream in = this.getClass().getResourceAsStream("/path/to/keystore.jks")) {
            keyStore.load(in, "keystorePassword".toCharArray());
        }

        return HttpClient.newBuilder()
                .sslContext(sslContext(getKeyStore(keystore, keyStorePassword), getTrustStore(truststore, trustStorePassword)))
                .build();
    }

    public static SSLContext sslContext(final KeyManagerFactory keyManagerFactory,
                                        final TrustManagerFactory trustManagerFactory) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            return sslContext;
        } catch (Exception e) {
            log.error("An error has occurred: ", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static KeyManagerFactory getKeyStore(final String keystoreContent, final String keyStorePassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keystoreContent), keyStorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keyStorePassword.toCharArray());
            return kmf;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static TrustManagerFactory getTrustStore(final String truststoreContent, final String trustStorePassword) {
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(truststoreContent), trustStorePassword.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            return tmf;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

//    private static ExchangeFilterFunction logRequest() {
//        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
//            if (log.isDebugEnabled()) {
//                StringBuilder sb = new StringBuilder(">>> Log Request: \n")
//                        .append(clientRequest.method())
//                        .append(" ")
//                        .append(clientRequest.url());
//                clientRequest
//                        .headers()
//                        .forEach((name, values) -> values.forEach(value -> sb
//                                .append("\n")
//                                .append(name)
//                                .append(":")
//                                .append(value)));
//                log.debug(sb.toString());
//            }
//            return Mono.just(clientRequest);
//        });
//    }

//    private static ExchangeFilterFunction logResponse() {
//        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
//            if (log.isDebugEnabled()) {
//                StringBuilder sb = new StringBuilder(">>> Log Response: \n")
//                        .append("Status: ")
//                        .append(clientResponse.rawStatusCode());
//                clientResponse
//                        .headers()
//                        .asHttpHeaders()
//                        .forEach((key, value1) -> value1.forEach(value -> sb
//                                .append("\n")
//                                .append(key)
//                                .append(":")
//                                .append(value)));
//                log.debug(sb.toString());
//            }
//            return Mono.just(clientResponse);
//        });
//    }
}
