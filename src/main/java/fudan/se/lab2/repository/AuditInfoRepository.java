package fudan.se.lab2.repository;

import fudan.se.lab2.domain.AuditInfo;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Paper;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuditInfoRepository extends CrudRepository<AuditInfo,Long>{
    List<AuditInfo> findAuditInfosByConference(Conference conference);
    List<AuditInfo> findAuditInfosByPaper(Paper paper);
    AuditInfo findAuditInfoById(Long id);
}
