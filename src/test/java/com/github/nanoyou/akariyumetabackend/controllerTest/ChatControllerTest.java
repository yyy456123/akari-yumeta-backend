package com.github.nanoyou.akariyumetabackend.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nanoyou.akariyumetabackend.controller.ChatController;
import com.github.nanoyou.akariyumetabackend.dto.SendMessageDTO;
import com.github.nanoyou.akariyumetabackend.dto.user.UserDTO;
import com.github.nanoyou.akariyumetabackend.entity.Result;
import com.github.nanoyou.akariyumetabackend.entity.chat.Message;
import com.github.nanoyou.akariyumetabackend.entity.user.User;
import com.github.nanoyou.akariyumetabackend.service.ChatService;
import com.github.nanoyou.akariyumetabackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @MockBean
    @Spy
    private ChatService chatService;

    @MockBean
    @Spy
    private UserService userService;

    @InjectMocks
    private ChatController chatController;

    @Mock
    private MockMvc mockMvc;

    @Test
    public void testSendMessage() throws Exception {
        String userId = "testUser";
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setContent("Hello world!");
        User user = new User();
        user.setId("testSender");
        Message message = Message.builder()
                .content(sendMessageDTO.getContent())
                .senderID(user.getId())
                .receiverID(userId)
                .isRead(false)
                .sendTime(LocalDateTime.now())
                .build();

        when(userService.userExists(userId)).thenReturn(true);
        when(chatService.addMessage(any(Message.class))).thenReturn(message);

        mockMvc.perform(MockMvcRequestBuilders.post("/chat/message/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sendMessageDTO))
                        .requestAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ok").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("消息发送成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(sendMessageDTO.getContent()));
    }

    @Test
    public void testGetMessageList() throws Exception {
        String userId = "testUser";
        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setContent("Hello world!");
        User user = new User();
        user.setId("testSender");
        Message message = Message.builder()
                .content(sendMessageDTO.getContent())
                .senderID(user.getId())
                .receiverID(userId)
                .isRead(false)
                .sendTime(LocalDateTime.now())
                .build();

        when(chatService.getMessageListByPair(userId, user.getId())).thenReturn(Collections.singletonList(message));

        mockMvc.perform(MockMvcRequestBuilders.get("/chat/message/" + userId)
                        .requestAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ok").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("查看消息列表成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].content").value(message.getContent()));
    }

    // Add more test methods for the other methods in your controller
//    @Test
//    public void testMyChat() throws Exception {
//        User user = new User();
//        user.setId("testUser");
//        Pair<String, Message> pair = new Pair<>("testUser2", new Message());
//        ReflectionTestUtils.setField(pair, "first", "New Value");
//        when(chatService.getMyChat(user.getId())).thenReturn(Collections.singletonList(pair));
//        when(userService.getUserDTO(pair.getFirst())).thenReturn(Optional.of(new UserDTO("John Doe", 25, "john.doe@example.com")));
//
//        mockMvc = standaloneSetup(chatController).build();
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/my/chat")
//                        .requestAttr("user", user))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.ok").value(true))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("聊天列表查询成功"));
//    }




    @Test
    public void testReadMessage() throws Exception {
        String messageId = "testMessage";
        Message message = new Message();
        message.setId(messageId);
        when(chatService.read(messageId)).thenReturn(Optional.of(message));

        mockMvc.perform(MockMvcRequestBuilders.post("/chat/message/" + messageId + "/read"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ok").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("标记已读成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(messageId));

        when(chatService.read(messageId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/chat/message/" + messageId + "/read"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.ok").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("标记已读失败：消息不存在"));
    }

}
