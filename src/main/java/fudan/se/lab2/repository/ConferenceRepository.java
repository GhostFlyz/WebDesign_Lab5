package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    List<Conference> findConferencesByShortName(String shortName);
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<Conference> findConferencesByChair(User chair);

    //@Query
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<Conference> findConferencesByPcMemberContains(User pcMember);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<Conference> findConferencesByAuditStatus(String auditStatus);
    Page<Conference> findAll(Pageable pageable);
}
