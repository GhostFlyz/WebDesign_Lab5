package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaperRepository extends CrudRepository<Paper, Long> {
    Paper findPaperByTitle(String title);
    List<Paper> findPapersByAuthor(User author);
    Paper findPaperById(Long id);
    List<Paper> findPapersByInspectorsContains(User inspector);
    List<Paper> findPapersByConference(Conference conference);
}
