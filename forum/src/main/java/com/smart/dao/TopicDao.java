package com.smart.dao;

import com.smart.domain.Topic;
import org.springframework.stereotype.Repository;

@Repository
public class TopicDao extends BaseDao<Topic> {
    private static final String GET_BOARD_DIGEST_TOPICS = "from Topic t where t.boardID" +
            "=? and digest>0 order by t.lastPost desc, digest desc";
    private static final String GET_PAGED_TOPICS = "from Topic where boardID = ?" +
            " order by lastPost desc";
    private static final String QUERY_TOPIC_BY_TITLE = "from Topic where topicTitle like ?" +
            " order by lastPost desc";

    public Page getBoardDigestTopics(int boardID, int pageNo, int pageSize) {
        return pagedQuery(GET_BOARD_DIGEST_TOPICS, pageNo, pageSize, boardID);
    }

    public Page getPagedTopics(int boardID, int pageNo, int pageSize) {
        return pagedQuery(GET_PAGED_TOPICS, pageNo, pageSize, boardID);
    }

    public Page queryTopicByTitle(String title, int pageNo, int pageSize) {
        return pagedQuery(QUERY_TOPIC_BY_TITLE, pageNo, pageSize, title);
    }
}
