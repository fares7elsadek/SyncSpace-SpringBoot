package com.fares7elsadek.syncspace;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class SyncspaceApplicationTests {
    ApplicationModules modules = ApplicationModules.of(SyncspaceApplication.class);

	@Test
	void contextLoads() {
        modules.verify();
	}

}
