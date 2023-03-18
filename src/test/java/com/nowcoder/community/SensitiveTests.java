package com.nowcoder.community;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private MessageService messageService;

    @Test
    public void testSensitiveFilter(){
        String text = "---我是习近平";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

    @Test
    public void testAddMessage(){
        Message msg = new Message();
        msg.setFromId(111);
        msg.setToId(112);
        msg.setConversationId(111 + "_" + 112);
        msg.setContent("私信测试");
        msg.setStatus(1);
        msg.setCreateTime(new Date());
        messageService.addMessage(msg);
    }
}
