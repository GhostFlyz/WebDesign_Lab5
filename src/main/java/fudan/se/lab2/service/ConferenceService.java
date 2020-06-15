package fudan.se.lab2.service;

import com.google.common.collect.Lists;
import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ConferenceService {
    private static final String YYYY_MM_DD_STRING
            = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-" +
            "(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|" +
            "((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
    private static final Pattern YYYY_MM_DD_PATTERN = Pattern.compile(YYYY_MM_DD_STRING);
    private static final Random random = new Random();
    private static final String SUCCESS = "success";
    private static final String REJECT = "reject";
    private static final String NOT_CHAIR = "user is not the conference's chair";
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PaperRepository paperRepository;
    private final Authority adminAuthority;
    private final TopicRepository topicRepository;
    private final Map<String, Integer> months;

    @Autowired
    public ConferenceService(ConferenceRepository conferenceRepository, UserRepository userRepository,
                             AuthorityRepository authorityRepository, PaperRepository paperRepository,
                             TopicRepository topicRepository) {
        /**
         * 一月 January 二月 February 三月 March 四月 April 五月 May 六月 June 七月 July
         * 八月 August 九月 September 十月 October 十一月 November 十二月 December
         * */
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.paperRepository = paperRepository;
        this.topicRepository = topicRepository;
        this.adminAuthority = authorityRepository.findByAuthority("Admin");
        months = new HashMap<>();
        months.put("January", 1);
        months.put("February", 2);
        months.put("March", 3);
        months.put("April", 4);
        months.put("May", 5);
        months.put("June", 6);
        months.put("July", 7);
        months.put("August", 8);
        months.put("September", 9);
        months.put("October", 10);
        months.put("November", 11);
        months.put("December", 12);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    //@CachePut(cacheNames = "conference")
    public String processApply(ConferenceRequest request) {
        /**
         * 举办时间格式：October 11-5,2020
         * 截稿日期和发奖日期格式：YYYY-MM-DD
         * */
        String shortName = request.getShortName();
        String fullName = request.getFullName();
        String position = request.getPosition();
        String time = request.getTime();
        String ddl = request.getDdlForSubmit();
        String rls = request.getReleaseDate();
        User chair = userRepository.findByUsername(request.getUsername());
        List<Conference> conference = conferenceRepository.findConferencesByShortName(shortName);

        if (conference != null && !conference.isEmpty() &&
                conference.get(0) != null && conference.get(0).getShortName() != null
                && shortName.equals(conference.get(0).getShortName())) {
            return "conference already exists";
        }

        String checkForTime = checkForTime(time, ddl, rls);
        if (!checkForTime.equals("valid")) return checkForTime;

        Conference confToSave = new Conference(shortName, fullName, time, position, ddl, rls, chair);
        conferenceRepository.saveAndFlush(confToSave);

        Set<String> topicStrings = request.getTopics();
        if (topicStrings == null || topicStrings.isEmpty()) {
            return "topic should be chosen";
        }
        for (String topicString : topicStrings) {
            Topic topic = new Topic();
            topic.setTopicName(topicString);
            topic.setConference(confToSave);
            topicRepository.saveAndFlush(topic);
        }
        conferenceRepository.flush();
        topicRepository.flush();
        return SUCCESS;
    }

    //@CachePut(cacheNames = "conference")
    public String auditConference(String conferenceShortName, String username, String status) {
        List<Conference> conferences = conferenceRepository.findConferencesByShortName(conferenceShortName);
        if (conferences == null || conferences.isEmpty()) {
            return "the conference does not exists";
        }
        Conference conference = conferences.get(0);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "the user does not exists";
        }
        if (!adminAuthority.getUsers().contains(user)) {
            return "the user is not admin";
        }
        if (!(status.equals("pass") || status.equals(REJECT))) {
            return "the status should be either pass or reject";
        }
        conference.setAuditStatus(status);
        if (status.equals(REJECT)) {
            conferenceRepository.delete(conference);
            return SUCCESS;
        }
        User chair = conference.getChair();
        conference.addToPCMember(chair);
        chair.addToPcMemberConfs(conference);
        conferenceRepository.save(conference);
        userRepository.save(chair);
        return SUCCESS;
    }

    public ResponseObject<List<Conference>> findConferenceByChairUser(String username) {
        User chair = userRepository.findByUsername(username);
        List<Conference> conferences = conferenceRepository.findConferencesByChair(chair);
        conferences.removeIf(conference -> REJECT.equals(conference.getAuditStatus()));
        return new ResponseObject<>(200, SUCCESS, conferences);
    }

    public ResponseObject<List<Conference>> findConferenceByParticipation(String username) {
        User user = userRepository.findByUsername(username);
        List<Conference> conferencesChair = conferenceRepository.findConferencesByChair(user);
        List<Conference> conferences = conferenceRepository.findConferencesByPcMemberContains(user);
        conferences.removeAll(conferencesChair);
        conferences.addAll(conferencesChair);
        return new ResponseObject<>(200, SUCCESS, conferences);
    }

    public ResponseObject<List<String>> findCharacterByConferenceAndUser(String username, String conferenceShortName) {
        User user = userRepository.findByUsername(username);
        List<Conference> conferences = conferenceRepository.findConferencesByShortName(conferenceShortName);
        if (conferences == null || conferences.isEmpty()) {
            return new ResponseObject<>(404, "conference not exist", null);
        }
        Conference conference = conferences.get(0);

        List<String> characterList = new LinkedList<>();
        if (conference.getAuthors() != null && conference.getAuthors().contains(user)) characterList.add("Author");
        if (conference.getPcMember() != null && conference.getPcMember().contains(user)) characterList.add("PC Member");
        if (user.equals(conference.getChair())) characterList.add("Chair");
        return new ResponseObject<>(200, SUCCESS, characterList);
    }

    //@CachePut(cacheNames = "conference")
    public String setConferenceCanBeSubmitted(String username, String conferenceShortName) {
        User user = userRepository.findByUsername(username);
        if (conferenceRepository.findConferencesByShortName(conferenceShortName).isEmpty()) {
            return "conference does not exist";
        }
        Conference conference = conferenceRepository.findConferencesByShortName(conferenceShortName).get(0);
        if (!user.equals(conference.getChair())) {
            return NOT_CHAIR;
        }
        if (!conference.getAuditStatus().equals("pass")) {
            return "the conference has not be audited";
        }
        conference.setCanBeSubmitted(true);
        conferenceRepository.save(conference);
        return SUCCESS;
    }

    public ResponseObject<Object> audit_DivideByTopics(String username, String conferenceShortName) {
        User user = userRepository.findByUsername(username);
        Conference conference = conferenceRepository.findConferencesByShortName(conferenceShortName).get(0);
        List<Paper> papers = paperRepository.findPapersByConference(conference);
        Set<User> PCMembers = conference.getPcMember();
        List<User> pcMembers = new ArrayList<>(PCMembers);
        List<User> auditPC;
        if (!user.equals(conference.getChair())) {
            return new ResponseObject<>(403, NOT_CHAIR, null);
        }
        if (conference.getPcMember().size() < 3) {
            return new ResponseObject<>(403, "this conference has less than 3 PCMembers", null);
        }
        conference.setCanBeSubmitted(false);
        conferenceRepository.save(conference);
        for (Paper paper : papers) {
            List<User> topicmembers = new LinkedList<>();
            for (User user1 : pcMembers) {
                if (checkTopics(paper.getTopics(), user1.getTopics())) {
                    topicmembers.add(user1);
                }
            }
            if (topicmembers.size() < 3 && !topicmembers.isEmpty()) {
                auditPC = checkPC(paper, pcMembers);
            } else {
                auditPC = checkPC(paper, topicmembers);
            }
            if (auditPC.size() < 3) {
                return new ResponseObject<>(403, "no proper division method", null);
            } else {
                DivideMethod(paper, auditPC);
            }
        }
        return new ResponseObject<>(200, SUCCESS, null);
    }

    public ResponseObject<Object> audit_DivideAverage(String username, String conferenceShortName) {
        User user = userRepository.findByUsername(username);
        Conference conference = conferenceRepository.findConferencesByShortName(conferenceShortName).get(0);
        List<Paper> papers = paperRepository.findPapersByConference(conference);
        Set<User> PCMembers = conference.getPcMember();
        if (!user.equals(conference.getChair())) {
            return new ResponseObject<>(403, NOT_CHAIR, null);
        }
        if (conference.getPcMember().size() < 3) {
            return new ResponseObject<>(403, "this conference has less than 3 PCMembers", null);
        }
        conference.setCanBeSubmitted(false);
        conferenceRepository.save(conference);
        List<User> pcMembers = new ArrayList<>(PCMembers);
        List<User> auditPC;
        for (Paper paper : papers) {
            auditPC = checkPC(paper, pcMembers);
            if (auditPC.size() < 3) {
                return new ResponseObject<>(403, "no proper division method", null);
            } else {
                DivideMethod(paper, auditPC);
            }
            if (pcMembers.size() < 3) {
                pcMembers = new ArrayList<>(PCMembers);
            }
        }
        return new ResponseObject<>(200, SUCCESS, null);
    }

    private boolean checkTopics(Set<Topic> topics1, Set<Topic> topics2) {
        for (Topic topic : topics1) {
            if (topics2.contains(topic)) {
                return true;
            }
        }
        return false;
    }

    private void DivideMethod(Paper paper, List<User> pcMembers) {
        int random1 = random.nextInt(pcMembers.size());
        User user1 = pcMembers.remove(random1);
        user1.addToPaperToAudited(paper);
        paper.addToInspectors(user1);

        int random2 = random.nextInt(pcMembers.size());
        User user2 = pcMembers.remove(random2);
        user2.addToPaperToAudited(paper);
        paper.addToInspectors(user2);

        int random3 = random.nextInt(pcMembers.size());
        User user3 = pcMembers.remove(random3);
        user3.addToPaperToAudited(paper);
        paper.addToInspectors(user3);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        paperRepository.save(paper);
    }

    private List<User> checkPC(Paper paper, List<User> pcMembers) {
        List<AuthorInfo> authorInfos = paper.getAuthorInfos();
        List<User> authors = new ArrayList<>();
        User author = paper.getAuthor();
        for (AuthorInfo authorInfo : authorInfos) {
            String fullname = authorInfo.getFullName();
            String email = authorInfo.getEmail();
            User user = userRepository.findUserByFullnameAndEmail(fullname, email);
            authors.add(user);
        }
        authors.add(author);
        return removeRepeatFactor(authors, pcMembers);
    }

    private List<User> removeRepeatFactor(List<User> list1, List<User> list2) {
        if (list1 != null && list2 != null) {
            if (list1.size() != 0 && list2.size() != 0) {
                Collection A = new ArrayList(list1);
                Collection B = new ArrayList(list2);
                A.retainAll(B);
                if (A.size() != 0) {
                    B.removeAll(A);
                }
                return (List<User>) B;
            }
        }
        return list2;
    }


    public ResponseObject<List<Conference>> findAllConferences() {
        return new ResponseObject<>(200, SUCCESS, Lists.newArrayList(conferenceRepository.findAll()));
    }

    public ResponseObject<Page<Conference>> findAllConferenceByPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Conference> conferencePage = conferenceRepository.findAll(pageable);
        return new ResponseObject<>(200, SUCCESS, conferencePage);
    }

    public ResponseObject<List<Conference>> findAllConferenceByUser(String username) {
        User user = userRepository.findByUsername(username);
        List<Conference> conferences = Lists.newArrayList(conferenceRepository.findAll());
        List<Conference> res = new LinkedList<>();
        for (Conference c : conferences) {
            if (c.getChair() != null && c.getAuthors() != null && c.getPcMember() != null &&
                    (c.getChair().equals(user) || c.getAuthors().contains(user) || c.getPcMember().contains(user)))
                res.add(c);
        }
        return new ResponseObject<>(200, SUCCESS, res);
    }

    public ResponseObject<List<Conference>> findAllConferenceNotAudited() {
        List<Conference> conferences = conferenceRepository.findConferencesByAuditStatus("wait");
        return new ResponseObject<>(200, SUCCESS, conferences);
    }

    public ResponseObject<Conference> pcMemberChooseTopic(String conferenceShortName,
                                                          String username, String topicName) {
        User user = userRepository.findByUsername(username);
        if (user == null) return new ResponseObject<>(404, "user does not exist", null);
        List<Conference> conferences = conferenceRepository.findConferencesByShortName(conferenceShortName);
        if (conferences.isEmpty()) return new ResponseObject<>(404, "conference does not exist", null);
        Conference conference = conferences.get(0);
        List<Topic> topics = topicRepository.findTopicsByTopicNameAndAndConference(topicName, conference);
        if (topics.isEmpty()) return new ResponseObject<>(404, "topic does not exist", null);
        Topic topic = topics.get(0);
        if (!conference.getPcMember().contains(user)) {
            return new ResponseObject<>(403, "the user is not pc member of this conference", null);
        }
        topic.addToUsers(user);
        user.addToTopics(topic);
        topicRepository.save(topic);
        userRepository.save(user);
        return new ResponseObject<>(200, SUCCESS, conference);
    }

    private String checkForTime(String time, String ddl, String rls) {
        /**
         * Time like 'October 5-11,2020' is valid
         * The rule is 'Month Date-Hour,Year'
         * (I guess)
         */

        if (!YYYY_MM_DD_PATTERN.matcher(ddl).matches()) return "ddl for submit is not a date";
        if (!YYYY_MM_DD_PATTERN.matcher(rls).matches()) return "release date is not a date";
        String[] ddlParts = ddl.split("-");
        String[] rlsParts = rls.split("-");
        String[] parts = time.split(" ");
        String[] nums = parts[1].split(",");
        String[] dateAndHour = nums[0].split("-");
        String month = parts[0];
        if (!months.containsKey(month)) {
            return "hold month name invalid! (first letter should be uppercase)";
        }
        int monthNum = months.get(month);
        int date;
        int hour;
        int year;
        try {
            date = Integer.parseInt(dateAndHour[0]);
        } catch (NumberFormatException e) {
            return "the hold date is not a number";
        }
        try {
            hour = Integer.parseInt(dateAndHour[1]);
        } catch (NumberFormatException e) {
            return "the hold hour is not a number";
        }
        try {
            year = Integer.parseInt(nums[1]);
        } catch (NumberFormatException e) {
            return "the hold year is not a number";
        }
        int ddlYear;
        int ddlMonth;
        int ddlDate;
        int rlsYear;
        int rlsMonth;
        int rlsDate;
        try {
            ddlYear = Integer.parseInt(ddlParts[0]);
            ddlMonth = Integer.parseInt(ddlParts[1]);
            ddlDate = Integer.parseInt(ddlParts[2]);
            rlsYear = Integer.parseInt(rlsParts[0]);
            rlsMonth = Integer.parseInt(rlsParts[1]);
            rlsDate = Integer.parseInt(rlsParts[2]);
        } catch (IndexOutOfBoundsException e) {
            return "hold time format error";
        }
        if (!dateBigger(rlsYear, rlsMonth, rlsDate, ddlYear, ddlMonth, ddlDate)) {
            return "the deadline for submit should be earlier than release";
        }
        if (!dateBigger(year, monthNum, date, rlsYear, rlsMonth, rlsDate)) {
            return "the release time should be earlier than hold time";
        }
        if (!dateValid(month, date, year)) return "the hold date is invalid";
        if (year < 0) return "the hold year is invalid";
        if (hour < 0 || hour > 24) return "the hold hour is invalid";
        return "valid";

    }

    private boolean dateBigger(int year1, int month1, int date1, int year2, int month2, int date2) {
        if (year1 < year2 || month1 < month2) return false;
        return !(year1 == year2 && month1 == month2 && date1 <= date2);
    }

    private boolean dateValid(String month, int date, int year) {
        boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
        switch (month) {
            case "January":
            case "March":
            case "May":
            case "July":
            case "August":
            case "October":
            case "December":
                return date > 0 && date <= 31;
            case "February":
                return isLeapYear ? date > 0 && date <= 29 : date > 0 && date <= 28;
            default:
                return date > 0 && date <= 30;
        }
    }

    public void helper() {
        Conference conference = conferenceRepository.findConferencesByShortName("test1587618879125").get(0);
        User user = userRepository.findByUsername("Raccoon");
        conference.addToPCMember(user);
        user.getPcMemberConfs().add(conference);
        conferenceRepository.save(conference);
        userRepository.save(user);
    }
}
