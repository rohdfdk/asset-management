package org.example.assetmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/db")
    public Map<String, Object> checkDatabase() {
        Map<String, Object> response = new HashMap<>();

        try {
            // データベース接続確認
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            response.put("status", "UP");
            response.put("database", "PostgreSQL");

            // 現在のタイムスタンプ取得
            String timestamp = jdbcTemplate.queryForObject(
                    "SELECT NOW()::text",
                    String.class
            );
            response.put("timestamp", timestamp);

            // データベース名確認
            String dbName = jdbcTemplate.queryForObject(
                    "SELECT current_database()",
                    String.class
            );
            response.put("database_name", dbName);

            // PostgreSQLバージョン確認
            String version = jdbcTemplate.queryForObject(
                    "SELECT version()",
                    String.class
            );
            response.put("version", version);

        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
        }

        return response;
    }
}