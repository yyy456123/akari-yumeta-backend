package com.github.nanoyou.akariyumetabackend.serviceTest;

import com.github.nanoyou.akariyumetabackend.dao.SubscriptionDao;
import com.github.nanoyou.akariyumetabackend.entity.friend.Subscription;
import com.github.nanoyou.akariyumetabackend.service.SubscriptionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubscriptionServiceTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @MockBean
    private SubscriptionDao subscriptionDao;

    @Test
    public void testGetFolloweeIDs() {
        // 给定
        String followerID = "testFollowerID";
        Subscription subscription1 = new Subscription();
        subscription1.setCombinedPrimaryKey(new Subscription._CombinedPrimaryKey(followerID, "testFolloweeID1"));
        Subscription subscription2 = new Subscription();
        subscription2.setCombinedPrimaryKey(new Subscription._CombinedPrimaryKey(followerID, "testFolloweeID2"));
        given(subscriptionDao.findByCombinedPrimaryKeyFollowerID(followerID)).willReturn(Arrays.asList(subscription1, subscription2));
        // 当
        List<String> followeeIDs = subscriptionService.getFolloweeIDs(followerID);
        // 则
        assertThat(followeeIDs).containsExactly("testFolloweeID1", "testFolloweeID2");
    }

    @Test
    public void testFollow() {
        // 给定
        Subscription subscription = new Subscription();
        subscription.setCombinedPrimaryKey(new Subscription._CombinedPrimaryKey("testFollowerID", "testFolloweeID"));
        given(subscriptionDao.save(any(Subscription.class))).willReturn(subscription);
        // 当
        Optional<Subscription> returnedSubscription = subscriptionService.follow(subscription);
        // 则
        assertThat(returnedSubscription).isNotEmpty();
        assertThat(returnedSubscription.get().getCombinedPrimaryKey()).isEqualTo(subscription.getCombinedPrimaryKey());
    }

    @Test
    public void testValidateFollow() {
        // 给定
        Subscription._CombinedPrimaryKey combinedPrimaryKey = new Subscription._CombinedPrimaryKey("testFollowerID", "testFolloweeID");
        given(subscriptionDao.findByCombinedPrimaryKey(combinedPrimaryKey)).willReturn(Optional.of(new Subscription()));
        // 当
        Boolean isValid = subscriptionService.validateFollow(combinedPrimaryKey);
        // 则
        assertThat(isValid).isTrue();
    }

    @Test
    public void testUnfollow() {
        // 给定
        Subscription._CombinedPrimaryKey combinedPrimaryKey = new Subscription._CombinedPrimaryKey("testFollowerID", "testFolloweeID");
        given(subscriptionDao.deleteByCombinedPrimaryKey(combinedPrimaryKey)).willReturn(true);
        // 当
        Boolean isUnfollowed = subscriptionService.unfollow(combinedPrimaryKey);
        // 则
        assertThat(isUnfollowed).isTrue();
        verify(subscriptionDao, times(1)).deleteByCombinedPrimaryKey(combinedPrimaryKey);
    }
}
