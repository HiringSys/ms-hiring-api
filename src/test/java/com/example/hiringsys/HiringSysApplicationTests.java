package com.example.hiringsys;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.supabase.url=http://localhost",
        "app.supabase.secret-key=test-secret",
        "app.supabase.bucket=test-bucket",
        "app.security.jwt.secret=test-secret-with-at-least-32-characters",
        "app.frontend.origin=http://localhost:5173",
        "spring.datasource.url=jdbc:h2:mem:hiringsys;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class HiringSysApplicationTests {

    @Test
    void contextLoads() {
    }

}
