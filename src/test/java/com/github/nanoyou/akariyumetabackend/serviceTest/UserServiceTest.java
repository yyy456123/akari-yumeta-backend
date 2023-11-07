package com.github.nanoyou.akariyumetabackend.serviceTest;

import com.github.nanoyou.akariyumetabackend.dao.TagDao;
import com.github.nanoyou.akariyumetabackend.dao.UserDao;
import com.github.nanoyou.akariyumetabackend.dto.user.LoginDTO;
import com.github.nanoyou.akariyumetabackend.dto.user.RegisterDTO;
import com.github.nanoyou.akariyumetabackend.dto.user.UserDTO;
import com.github.nanoyou.akariyumetabackend.entity.user.Tag;
import com.github.nanoyou.akariyumetabackend.entity.user.User;
import com.github.nanoyou.akariyumetabackend.dto.TagDTO;
import com.github.nanoyou.akariyumetabackend.service.TagService;
import com.github.nanoyou.akariyumetabackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private TagService tagService;

    @Mock
    private UserService userService;

    // 创建模拟的tagDao对象
    TagDao tagDao = Mockito.mock(TagDao.class);



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            Constructor<UserService> constructor = UserService.class.getDeclaredConstructor(UserDao.class, TagService.class);
            constructor.setAccessible(true);
            userService = constructor.newInstance(userDao, tagService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void testGetUser() {
        String userID = "123";
        User user = new User();
        when(userDao.findById(userID)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUser(userID);

        assertEquals(Optional.of(user), result);
        verify(userDao, times(1)).findById(userID);
    }

    @Test
    void testGetUserDTO() {
        String userID = "123";
        User user = new User();
        user.setId(userID);
        user.setUsername("testuser");
        user.setNickname("Test User");
        // Set other properties of the user object

        TagDTO tagDTO = TagDTO.builder().build();
        tagDTO.setTagContentList(new ArrayList<>());
        when(userDao.findById(userID)).thenReturn(Optional.of(user));
        when(tagService.getTags(userID)).thenReturn(tagDTO);

        Optional<UserDTO> result = userService.getUserDTO(userID);

        UserDTO expectedDTO = UserDTO.builder()
                .id(userID)
                .username("testuser")
                .nickname("Test User")
                // Set other properties of the expectedDTO object
                .tags(new ArrayList<>())
                .build();

        assertEquals(Optional.of(expectedDTO), result);
        verify(userDao, times(1)).findById(userID);
        verify(tagService, times(1)).getTags(userID);
    }

    // Write similar tests for other methods in the UserService class
    @Test
    void testGetAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        when(userDao.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertEquals(userList, result);
        verify(userDao, times(1)).findAll();
    }

    @Test
    void testAddTags() {
        // 创建测试数据
        String userID = "f2f976ec-995a-19f4-e95f-3b1ec0b2e550";
        List<String> tagContentList = Arrays.asList("tag1", "tag2", "tag3");

        // 创建模拟的tagList和tagContentList
        List<Tag> tagList = Arrays.asList(
                Tag.builder()
                        .combinedPrimaryKey(Tag.CombinedPrimaryKey.builder()
                                .userId(userID)
                                .tagContent("tag1")
                                .build())
                        .build(),
                Tag.builder()
                        .combinedPrimaryKey(Tag.CombinedPrimaryKey.builder()
                                .userId(userID)
                                .tagContent("tag2")
                                .build())
                        .build(),
                Tag.builder()
                        .combinedPrimaryKey(Tag.CombinedPrimaryKey.builder()
                                .userId(userID)
                                .tagContent("tag3")
                                .build())
                        .build()
        );

        // 模拟tagDao的saveAllAndFlush()方法返回tagList
        when(tagDao.saveAllAndFlush(anyList())).thenReturn(tagList);

        // 调用addTags()方法
        TagDTO result = tagService.addTags(userID, tagContentList);

        // 验证tagList对象是否不为null
        assertNotNull(tagList);

        // 验证返回的TagDTO对象是否不为null
        assertNotNull(result);
        assertEquals(userID, result.getUserID());
        assertEquals(tagContentList, result.getTagContentList());

        // 验证tagDao的saveAllAndFlush()方法是否被调用一次，并且传入了正确的参数
        verify(tagDao, times(1)).saveAllAndFlush(anyList());
    }



    @Test
    void testLogin() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password");

        User user = new User();
        user.setId("123");
        user.setUsername("testuser");
        user.setNickname("Test User");
        // Set other properties of the user object

        TagDTO tagDTO = TagDTO.builder().build();
        tagDTO.setTagContentList(new ArrayList<>());
        when(userDao.findByUsernameAndPassword("testuser", "password")).thenReturn(Optional.of(user));
        when(tagService.getTags("123")).thenReturn(tagDTO);

        Optional<UserDTO> result = userService.login(loginDTO);

        UserDTO expectedDTO = UserDTO.builder()
                .id("123")
                .username("testuser")
                .nickname("Test User")
                // Set other properties of the expectedDTO object
                .tags(new ArrayList<>())
                .build();

        assertEquals(Optional.of(expectedDTO), result);
        verify(userDao, times(1)).findByUsernameAndPassword("testuser", "password");
        verify(tagService, times(1)).getTags("123");
    }

}
