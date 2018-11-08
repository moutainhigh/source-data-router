package com.globalegrow.dy.config;

import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.elasticsearch.jest.HttpClientConfigBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

//@Configuration
@ConditionalOnClass(JestClient.class)
@EnableConfigurationProperties(JestProperties.class)
@AutoConfigureAfter(GsonAutoConfiguration.class)
public class MyJestAutoConfig {

    private final JestProperties properties;

    private final ObjectProvider<Gson> gsonProvider;

    private final List<HttpClientConfigBuilderCustomizer> builderCustomizers;

    public MyJestAutoConfig(JestProperties properties, ObjectProvider<Gson> gson,
                                 ObjectProvider<List<HttpClientConfigBuilderCustomizer>> builderCustomizers) {
        this.properties = properties;
        this.gsonProvider = gson;
        this.builderCustomizers = builderCustomizers.getIfAvailable();
    }

    @Bean(destroyMethod = "shutdownClient", name = "myJestClient")
    @ConditionalOnMissingBean
    @Qualifier("myJestClient")
    public JestClient jestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(createHttpClientConfig());
        return factory.getObject();
    }

    protected HttpClientConfig createHttpClientConfig() {
        HttpClientConfig.Builder builder = new HttpClientConfig.Builder(
                this.properties.getUris());
        if (StringUtils.hasText(this.properties.getUsername())) {
            builder.defaultCredentials(this.properties.getUsername(),
                    this.properties.getPassword());
        }
        String proxyHost = this.properties.getProxy().getHost();
        if (StringUtils.hasText(proxyHost)) {
            Integer proxyPort = this.properties.getProxy().getPort();
            Assert.notNull(proxyPort, "Proxy port must not be null");
            builder.proxy(new HttpHost(proxyHost, proxyPort));
        }
        Gson gson = this.gsonProvider.getIfUnique();
        if (gson != null) {
            builder.gson(gson);
        }
        builder.multiThreaded(this.properties.isMultiThreaded());
        builder.connTimeout(this.properties.getConnectionTimeout())
                .readTimeout(this.properties.getReadTimeout());
        builder.defaultMaxTotalConnectionPerRoute(1000);
        builder.maxTotalConnection(2500);
        customize(builder);
        return builder.build();
    }

    private void customize(HttpClientConfig.Builder builder) {
        if (this.builderCustomizers != null) {
            for (HttpClientConfigBuilderCustomizer customizer : this.builderCustomizers) {
                customizer.customize(builder);
            }
        }
    }

}
