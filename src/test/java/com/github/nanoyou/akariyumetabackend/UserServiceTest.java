package com.github.nanoyou.akariyumetabackend;

import com.github.nanoyou.akariyumetabackend.dao.UserDao;
import com.github.nanoyou.akariyumetabackend.dto.user.LoginDTO;
import com.github.nanoyou.akariyumetabackend.dto.user.RegisterDTO;
import com.github.nanoyou.akariyumetabackend.dto.user.UserDTO;
import com.github.nanoyou.akariyumetabackend.entity.user.User;
import com.github.nanoyou.akariyumetabackend.dto.TagDTO;
import com.github.nanoyou.akariyumetabackend.service.TagService;
import com.github.nanoyou.akariyumetabackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private TagService tagService;

    @Mock
    private UserService userService;

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
    void testRegister() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setNickname("Test User");
        // Set other properties of the registerDTO object

        User user = new User();
        user.setId("123");
        user.setUsername("testuser");
        user.setNickname("Test User");
        // Set other properties of the user object

        TagDTO tagDTO = TagDTO.builder().build();
        tagDTO.setTagContentList(new ArrayList<>());
        when(userDao.saveAndFlush(any(User.class))).thenReturn(user);
        when(tagService.addTags(eq("123"), anyList())).thenReturn(tagDTO);

        UserDTO result = userService.register(registerDTO);

        UserDTO expectedDTO = UserDTO.builder()
                .id("123")
                .username("testuser")
                .nickname("Test User")
                // Set other properties of the expectedDTO object
                .tags(new ArrayList<>())
                .build();

        assertEquals(expectedDTO, result);
        verify(userDao, times(1)).saveAndFlush(any(User.class));
        verify(tagService, times(1)).addTags(eq("123"), anyList());
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
