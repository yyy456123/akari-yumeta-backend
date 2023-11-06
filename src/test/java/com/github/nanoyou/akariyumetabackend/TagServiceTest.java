package com.github.nanoyou.akariyumetabackend;

import com.github.nanoyou.akariyumetabackend.dao.TagDao;
import com.github.nanoyou.akariyumetabackend.dto.TagDTO;
import com.github.nanoyou.akariyumetabackend.entity.user.Tag;
import com.github.nanoyou.akariyumetabackend.service.TagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TagServiceTest {

    @Autowired
    private TagService tagService;

    @MockBean
    private TagDao tagDao;

    @Test
    public void testAddTags() {
        // 给定
        String userID = "testUserID";
        List<String> tagContentList = Arrays.asList("tag1", "tag2");
        List<Tag> tagList = Arrays.asList(
                new Tag(new Tag.CombinedPrimaryKey(userID, "tag1")),
                new Tag(new Tag.CombinedPrimaryKey(userID, "tag2"))
        );
        given(tagDao.saveAllAndFlush(any(List.class))).willReturn(tagList);
        // 当
        TagDTO returnedTagDTO = tagService.addTags(userID, tagContentList);
        // 则
        assertThat(returnedTagDTO.getUserID()).isEqualTo(userID);
        assertThat(returnedTagDTO.getTagContentList()).containsExactlyElementsOf(tagContentList);
    }

    @Test
    public void testGetTags() {
        // 给定
        String userID = "testUserID";
        List<String> tagContentList = Arrays.asList("tag1", "tag2");
        List<TagDao.TagContentProjection> tagContentProjections = Arrays.asList(
                () -> "tag1",
                () -> "tag2"
        );
        given(tagDao.findTagContentProjectionByCombinedPrimaryKeyUserId(userID)).willReturn(tagContentProjections);
        // 当
        TagDTO returnedTagDTO = tagService.getTags(userID);
        // 则
        assertThat(returnedTagDTO.getUserID()).isEqualTo(userID);
        assertThat(returnedTagDTO.getTagContentList()).containsExactlyElementsOf(tagContentList);
    }

}
