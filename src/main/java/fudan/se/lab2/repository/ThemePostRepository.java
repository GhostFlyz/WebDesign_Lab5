package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.domain.ThemePost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemePostRepository extends CrudRepository<ThemePost, Long> {
    ThemePost getThemePostsById(Long id);
    List<ThemePost> findThemePostsByPaperAndType(Paper paper, String type);
}
