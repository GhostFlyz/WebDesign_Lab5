package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.InvitationRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Invitation;
import fudan.se.lab2.domain.Topic;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.repository.InvitationRepository;
import fudan.se.lab2.repository.TopicRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class InvitationService {
    private InvitationRepository invitationRepository;
    private ConferenceRepository conferenceRepository;
    private UserRepository userRepository;
    private TopicRepository topicRepository;
    private static final String SUCCESS = "success";

    @Autowired
    public InvitationService(InvitationRepository invitationRepository,
                             ConferenceRepository conferenceRepository, UserRepository userRepository, TopicRepository topicRepository) {
        this.invitationRepository = invitationRepository;
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public String sendInvitation(InvitationRequest request) {
        String inviterName = request.getInviterName();
        String inviteeName = request.getInviteeName();
        String conferenceName = request.getConferenceName();
        User invitee = userRepository.findByUsername(inviteeName);
        if (invitee == null)
            return "the user does not exist";
        List<Conference> conferences = conferenceRepository.findConferencesByShortName(conferenceName);
        if (conferences == null || conferences.isEmpty())
            return "the conference does not exist";
        Conference conference = conferences.get(0);
        List<Invitation> invitations = invitationRepository.findInvitationsByInviteeAndConference(inviteeName, conferenceName);
        if (!invitations.isEmpty())
            return "the user has already been invited";
        Set<Topic> topics;
        if (conference.getTopics() == null) {
            topics = new HashSet<>(topicRepository.findTopicsByConference(conference));
        } else {
            topics = conference.getTopics();
        }
        Invitation invitation = new
                Invitation(inviterName, inviteeName, conference.getShortName(), new HashSet<>(topics));

        invitationRepository.save(invitation);
        for (Topic topic : topics) {
            topic.addToInvitations(invitation);
            topicRepository.save(topic);
        }
        return SUCCESS;
    }

    public String respondInvitation(String conferenceName, String inviteeName, String status, List<String> topics) {
        List<Conference> conferences = conferenceRepository.findConferencesByShortName(conferenceName);
        if (conferences == null || conferences.isEmpty()) {
            return "the conference does not exists";
        }
        Conference conference = conferences.get(0);
        User invitee = userRepository.findByUsername(inviteeName);
        if (!(status.equals("receive") || status.equals("reject"))) {
            return "the status should be either receive or reject";
        }
        List<Invitation> invitations = invitationRepository.findInvitationsByInviteeAndConference(inviteeName, conferenceName);
        if (invitations == null || invitations.isEmpty()) {
            return "the invitation does not exists";
        }
        Invitation invitation = invitations.get(0);
        invitation.setStatus(status);
        invitationRepository.save(invitation);
        if (status.equals("receive")) {
            conference.getPcMember().add(invitee);
            conferenceRepository.save(conference);
            invitee.getPcMemberConfs().add(conference);
            for (String topic : topics) {
                List<Topic> list = topicRepository.findTopicsByTopicNameAndAndConference(topic, conference);
                invitee.addToTopics(list.get(0));
            }
            userRepository.save(invitee);
        }
        return SUCCESS;
    }

    public ResponseObject<List<Invitation>> findInvitationsByInviter(String inviterName) {
        List<Invitation> invitations = invitationRepository.findInvitationsByInviter(inviterName);
        return new ResponseObject<>(200, SUCCESS, invitations);
    }

    public ResponseObject<List<Invitation>> findInvitationsByInvitee(String inviteeName) {
        List<Invitation> invitations = invitationRepository.findInvitationsByInviteeAndStatus(inviteeName, "wait");
        return new ResponseObject<>(200, SUCCESS, invitations);
    }

    public ResponseObject<List<User>> findUsersByUsernameContaining(String field, String username) {
        List<User> users = userRepository.findUsersByUsernameContaining(field);
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            String str = it.next().getUsername();
            if (username.equals(str)) {
                it.remove();
            }
        }
        return new ResponseObject<>(200, SUCCESS, users);
    }

    public ResponseObject<List<User>> findUsersByFullname(String fullname, String username) {
        List<User> users = userRepository.findUsersByFullnameContaining(fullname);
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            String str = it.next().getUsername();
            if (username.equals(str)) {
                it.remove();
            }
        }
        return new ResponseObject<>(200, SUCCESS, users);
    }
}
