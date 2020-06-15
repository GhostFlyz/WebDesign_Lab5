package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findTopicsByTopicNameAndAndConference(String topicName, Conference conference);
    List<Topic> findTopicsByConference(Conference conference);
}
