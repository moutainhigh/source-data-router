package com.globalegrow;

import com.globalegrow.dy.es.ElasticSearchRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("dev")
public class EsTest {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Test
    public void testIndexCreate() {
        this.elasticSearchRepository.createIndexWithoutAlias("wzf_temp_test", 3, 1);
    }

    @Test
    public void testIndexAddAlias() throws InterruptedException, ExecutionException, IOException {
        this.elasticSearchRepository.indexAddAlias("wzf_temp_test", "TestRunner");
    }

    @Test
    public void testDeleteIndex() {
        this.elasticSearchRepository.deleteIndex("TestRunner");
    }

    @Test
    public void testCreateIndexWithAlias() {
        this.elasticSearchRepository.createIndex("wzf_test_index", "TestRunner", 3, 1);
    }

}
