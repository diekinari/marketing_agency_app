package com.example.marketing_agency_app;

import com.example.marketing_agency_app.model.CampaignMetrics;
import com.example.marketing_agency_app.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CampaignRepositoryTest {

    @Autowired
    private CampaignRepository campaignRepository;

    // Если требуется, можно использовать скрипт для предварительной загрузки тестовых данных.
    // Например, поместите файл test-data.sql в src/test/resources и укажите аннотацию @Sql.
    // Пример:
    // @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    // В этом скрипте можно вставить тестовые данные в таблицы, из которых формируется представление campaign_metrics.
//
//    @BeforeEach
//    public void setUp() {
//        // Если вы не используете @Sql, можно программно добавить тестовые данные через EntityManager.
//        // Однако, если CampaignMetrics – read-only сущность, это может быть затруднительно.
//        // Предполагаем, что данные уже загружены в тестовую базу.
//    }

    @Test
    public void testFindByNameTest_ReturnsResults() {
        // Укажите искомую подстроку, например "campaign".
        String searchName = "Summer Bonanza";

        // Вызываем метод репозитория поиска по имени.
        List<CampaignMetrics> results = campaignRepository.findByNameTest(searchName);

        // Выводим в консоль количество найденных результатов (для дебага).
        System.out.println("Найдено результатов по имени '" + searchName + "': " + results.size());

        // Проверяем, что список не равен null.
        assertThat(results).isNotNull();

        // В зависимости от тестовых данных, можно сделать дополнительную проверку.
        // Например, если вы ожидаете по тестовым данным, что должно быть не менее 1 записи:
        assertThat(results).isNotEmpty();
    }
}
