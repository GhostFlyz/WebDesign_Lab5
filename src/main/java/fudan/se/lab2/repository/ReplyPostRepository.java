package fudan.se.lab2.repository;

import fudan.se.lab2.domain.ReplyPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyPostRepository extends CrudRepository<ReplyPost, Long> {

}
