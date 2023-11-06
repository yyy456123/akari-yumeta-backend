package com.github.nanoyou.akariyumetabackend;

import com.github.nanoyou.akariyumetabackend.dao.LikeDao;
import com.github.nanoyou.akariyumetabackend.entity.dynamic.Like;
import com.github.nanoyou.akariyumetabackend.service.LikeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LikeServiceTest {

    @Autowired
    private LikeService likeService;

    @MockBean
    private LikeDao likeDao;

    @Test
    public void testGetLikeCountByCommentID() {
        // 给定
        String commentID = "testCommentID";
        given(likeDao.findByCommentID(commentID)).willReturn(Arrays.asList(new Like(), new Like()));
        // 当
        int likeCount = likeService.getLikeCountByCommentID(commentID);
        // 则
        assertThat(likeCount).isEqualTo(2);
    }
}
