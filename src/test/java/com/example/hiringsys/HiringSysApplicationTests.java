package com.example.hiringsys;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.supabase.url=http://localhost",
        "app.supabase.secret-key=test-secret",
        "app.supabase.bucket=test-bucket",
        "app.security.admin.username=admin-test",
        "app.security.admin.password=admin-test-password",
        "app.security.rh.username=rh-test",
        "app.security.rh.password=rh-test-password",
        "app.security.jwt.secret=test-secret-with-at-least-32-characters",
        "app.frontend.origin=http://localhost:5173"
})
class HiringSysApplicationTests {

    @Test
    void contextLoads() {
    }

}
