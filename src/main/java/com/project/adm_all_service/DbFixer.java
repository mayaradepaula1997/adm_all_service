package com.project.adm_all_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DbFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Remove constraints antigas (idempotente via try/catch)
        try {
            jdbcTemplate.execute("ALTER TABLE tb_user ALTER COLUMN city_id DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE tb_user ALTER COLUMN enterprise_id DROP NOT NULL");
            System.out.println("====== DB CONSTRAINTS REMOVED SUCCESSFULLY ======");
        } catch (Exception e) {
            System.out.println("====== COULD NOT REMOVE DB CONSTRAINTS: " + e.getMessage() + " ======");
        }

        // Migração de city_id (coluna legada) para a tabela user_cities (ManyToMany)
        // ON CONFLICT DO NOTHING garante idempotência: roda sem efeito se os dados já existirem
        try {
            jdbcTemplate.execute("""
                    INSERT INTO user_cities (user_id, city_id)
                    SELECT id, city_id
                    FROM tb_user
                    WHERE city_id IS NOT NULL
                    ON CONFLICT DO NOTHING
                    """);
            System.out.println("====== USER_CITIES MIGRATION COMPLETED SUCCESSFULLY ======");
        } catch (Exception e) {
            System.out.println("====== USER_CITIES MIGRATION SKIPPED: " + e.getMessage() + " ======");
        }
    }
}
