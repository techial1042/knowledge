package top.techial.knowledge.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class NewNodeServerTest {
    @Autowired
    private NewNodeServer newNodeServer;

    @Test
    public void findByTitle() {
        System.out.println(newNodeServer.findByTitle("香草植物"));
    }

}
