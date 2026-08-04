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
        try {
            jdbcTemplate.execute("ALTER TABLE tb_user ALTER COLUMN city_id DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE tb_user ALTER COLUMN enterprise_id DROP NOT NULL");
            System.out.println("====== DB CONSTRAINTS REMOVED SUCCESSFULLY ======");
        } catch (Exception e) {
            System.out.println("====== COULD NOT REMOVE DB CONSTRAINTS: " + e.getMessage() + " ======");
        }
    }
}
