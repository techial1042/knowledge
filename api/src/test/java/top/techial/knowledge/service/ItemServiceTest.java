package top.techial.knowledge.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.techial.knowledge.domain.Item;
import top.techial.knowledge.domain.User;

import java.util.Collections;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        itemService.save(new Item().setName("item1").setAuthor(new User().setId(3)));
        itemService.save(new Item().setName("item2").setAuthor(new User().setId(3)));
        itemService.save(new Item().setName("item3").setAuthor(new User().setId(3)));
    }

    @Test
    public void save() {
        User user = userService.findById(3).orElseThrow(NullPointerException::new)
                .setItem(Collections.singleton(new Item().setId(1)));
        userService.save(user);
    }

}