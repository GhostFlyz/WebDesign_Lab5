package fudan.se.lab2.repository;

import fudan.se.lab2.domain.AuthorInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorInfoRepository extends CrudRepository<AuthorInfo, Long> {
    List<AuthorInfo> findAuthorInfosByFullNameAndEmail(String fullName, String email);
}
