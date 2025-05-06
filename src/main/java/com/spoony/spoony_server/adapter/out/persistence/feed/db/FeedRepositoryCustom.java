package com.spoony.spoony_server.adapter.out.persistence.feed.db;

import com.spoony.spoony_server.domain.feed.Feed;

import java.util.List;

public interface FeedRepositoryCustom {
    List<Feed> findFilteredFeeds(List<Long> categoryIds, List<Long> regionIds, boolean localReviewEnabled);
}