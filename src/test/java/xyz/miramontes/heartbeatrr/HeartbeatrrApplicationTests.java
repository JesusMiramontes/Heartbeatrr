package xyz.miramontes.heartbeatrr;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import xyz.miramontes.heartbeatrr.service.DiscordService;

@SpringBootTest
class HeartbeatrrApplicationTests {

    @MockBean private DiscordService discordService;

    @Test
    void contextLoads() {}
}
