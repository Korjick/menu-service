package ru.itlab.menuservice;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.itlab.menuservice.storage.repositories.updaters.MenuAttrUpdaterConfig;

@DataJpaTest
@Import(MenuAttrUpdaterConfig.class)
public class BaseDataTest extends BaseTestContainerTest {
}
