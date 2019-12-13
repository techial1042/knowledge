package top.techial.knowledge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.techial.knowledge.domain.User;
import top.techial.knowledge.service.KnowledgeNodeService;
import top.techial.knowledge.service.UserService;

@Component
public class Init implements CommandLineRunner {
    private final UserService userService;
    private final KnowledgeNodeService knowledgeNodeService;

    public Init(UserService userService, KnowledgeNodeService knowledgeNodeService) {
        this.userService = userService;
        this.knowledgeNodeService = knowledgeNodeService;
    }

    @Override
    public void run(String... args) {
        knowledgeNodeService.deleteAll();
        userService.deleteAll();
        if (userService.count() == 0L) {
            userService.save(new User().setUserName("root").setPassword("{noop}root"));
            userService.save(new User().setUserName("admin").setPassword("{noop}admin"));
            userService.save(new User().setUserName("techial").setPassword("{noop}techial"));
        }

    }
}
