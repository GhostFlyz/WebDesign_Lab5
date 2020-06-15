package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Invitation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InvitationRepository extends CrudRepository<Invitation, Long> {
    List<Invitation> findInvitationsByInviter(String inviter);

    List<Invitation> findInvitationsByInviteeAndStatus(String invitee, String status);

    List<Invitation> findInvitationsByInviteeAndConference(String invitee, String conference);
}
