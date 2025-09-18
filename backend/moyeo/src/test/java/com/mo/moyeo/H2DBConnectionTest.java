package com.mo.moyeo;

import com.mo.moyeo.common.config.QuerydslConfig;
import com.mo.moyeo.domain.bank.entity.Bank;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
public class H2DBConnectionTest {

    @Autowired
    EntityManager entityManager;

    @Test
    public void bankTest() {
        // given
        Bank ssafyBank = Bank.builder().code("999").bankName("싸피은행").build();
        entityManager.persist(ssafyBank);

        // when
        Bank findBank = entityManager.find(Bank.class, ssafyBank.getCode());

        // then
        Assertions.assertThat(findBank.getBankName()).isEqualTo(ssafyBank.getBankName());
    }

}
