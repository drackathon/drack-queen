package org.drack.hackathon.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
class PetDAOPostgres implements PetDAO{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PetDAOPostgres(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate, "JDBC template must not be null");
    }

    @Override
    public void writePetName(String name) {

    }
}
