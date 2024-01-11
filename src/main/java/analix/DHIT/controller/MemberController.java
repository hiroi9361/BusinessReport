package analix.DHIT.controller;


import analix.DHIT.input.*;
import analix.DHIT.model.*;
import analix.DHIT.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/member")
public class MemberController {

    private final UserService userService;
    private final TaskLogService taskLogService;
    private final ReportService reportService;
    private final FeedbackService feedbackService;
    private final AssignmentService assignmentService;
    private final TeamService teamService;
    private final SettingService settingService;

    private final MailService mailService;
//    @Autowired
    public MemberController(UserService userService,
                            TaskLogService taskLogService,
                            ReportService reportService,
                            FeedbackService feedbackService,
                            AssignmentService assignmentService,
                            TeamService teamService,
                            SettingService settingService, MailService mailService) {
        this.userService = userService;
        this.taskLogService = taskLogService;
        this.reportService = reportService;
        this.feedbackService = feedbackService;
        this.assignmentService = assignmentService;
        this.teamService = teamService;
        this.settingService = settingService;
        this.mailService = mailService;
    }

    @GetMapping("/report/create")
    public String displayReportCreate(
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //バリデーションInteger
        int employeeCode = Integer.parseInt(authentication.getName());
        //employeeCodeを使用し、直近のreportがあるか調べる(取得)
        String latestReportId = reportService.getLatestIdByEmployeeCode(employeeCode);
        ReportCreateInput reportCreateInput = new ReportCreateInput();
        SettingInput settingInput = new SettingInput();
        //java.timeパッケージから現在の時刻を取得
        reportCreateInput.setDate(LocalDate.now());

        String title = "報告作成";
        model.addAttribute("title", title);
        //規定の終業時間を取得し、セット
        Setting setting = settingService.getSettingTime(employeeCode);
        settingInput.setStartTime(setting.getStartTime());
        settingInput.setEndTime(setting.getEndTime());
        settingInput.setEmployment(false);
        model.addAttribute("settingInput", settingInput);
        if (latestReportId == null) {
            model.addAttribute("reportCreateInput", reportCreateInput);
            return "member/report-create";
        }
        //以下reportがひとつでもあった場合の処理
        //既存のreportidを参照にreportModelの値をすべてset
        Report report = reportService.getReportById(Integer.parseInt(latestReportId));
        //(前日のreport内容を引継ぎ入力欄に記入)
        reportCreateInput.setStartTime(report.getStartTime());
        reportCreateInput.setEndTime(report.getEndTime());
        //report_idを参照してtask_Logの値を取得しset
        reportCreateInput.setTaskLogs(taskLogService.getIncompleteTaskLogsByReportId(Integer.parseInt(latestReportId)));


        model.addAttribute("reportCreateInput", reportCreateInput);
        return "member/report-create";

    }

    //↓Transactionalはトランザクション処理で一連の流れが失敗した場合ロールバックする
    @Transactional
    @PostMapping("/report/create")
    public String createReport(ReportCreateInput reportCreateInput, RedirectAttributes redirectAttributes, SettingInput settingInput, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        //遅刻早退を判定
        Setting setting = settingService.getSettingTime(employeeCode);

        if (!settingInput.getEmployment()) {
            if (settingInput.getStartTime().isAfter(setting.getStartTime()) || settingInput.getEndTime().isBefore(setting.getEndTime())) {
                String reason = "";
                if (settingInput.getStartTime().isAfter(setting.getStartTime()) && settingInput.getEndTime().isBefore(setting.getEndTime())) {
                    reason = "※遅刻 及び 早退の理由を記入してください";
                    reportCreateInput.setIsLateness(true);
                    reportCreateInput.setIsLeftEarly(true);
                } else if (settingInput.getStartTime().isAfter(setting.getStartTime())) {
                    reason = "※遅刻の理由を記入してください";
                    reportCreateInput.setIsLateness(true);
                } else if (settingInput.getEndTime().isBefore(setting.getEndTime())) {
                    reason = "※早退の理由を記入してください";
                    reportCreateInput.setIsLeftEarly(true);
                }

                settingInput.setEmployment(true);

                model.addAttribute("settingInput", settingInput);
                model.addAttribute("reportCreateInput", reportCreateInput);
                String title = "報告作成";
                model.addAttribute("title", title);
                model.addAttribute("reason", reason);
                return "member/report-create";
            }
        }

        if (reportService.existsReport(employeeCode, reportCreateInput.getDate())) {
            redirectAttributes.addFlashAttribute("error", reportCreateInput.getDate() + "は既に業務報告書が存在しています");
            return "redirect:/member/report/create";
        }

        //遅刻・早退判定
        if (settingInput.getStartTime().isAfter(setting.getStartTime()) && settingInput.getEndTime().isBefore(setting.getEndTime())) {
            reportCreateInput.setIsLateness(true);
            reportCreateInput.setIsLeftEarly(true);
        } else if (settingInput.getStartTime().isAfter(setting.getStartTime())) {
            reportCreateInput.setIsLateness(true);
        } else if (settingInput.getEndTime().isBefore(setting.getEndTime())) {
            reportCreateInput.setIsLeftEarly(true);
        }

        //newReportIdには新たにInsertされたreportのIDが入る
        int newReportId = reportService.create(
                employeeCode,
                reportCreateInput.getCondition(),
                reportCreateInput.getImpressions(),
                reportCreateInput.getTomorrowSchedule(),
                reportCreateInput.getDate(),
                reportCreateInput.getEndTime(),
                reportCreateInput.getStartTime(),
                reportCreateInput.getIsLateness(),
                reportCreateInput.getLatenessReason(),
                reportCreateInput.getIsLeftEarly(),
                reportCreateInput.getConditionRate()
        );

        // タスクが存在するならタスクログに追加
        if (reportCreateInput.getTaskLogs() != null) {
            List<TaskLog> taskLogs = reportCreateInput.getTaskLogs();
            taskLogs.forEach(x -> x.setReportId(newReportId));
            for (TaskLog taskLog : taskLogs) {
                if (taskLog != null && taskLog.getName() != null) {
                    taskLogService.create(taskLog);
                }
            }
        }

        return "redirect:/member/report/create-completed";
    }

    @GetMapping("/report/create-completed")
    public String displayReportCreateCompleted(
    ) {
        return "member/report-create-completed";
    }

    @GetMapping("/report-search")
    public String displayReportSearch(
            Model model
    ) {


        String title = "報告一覧";
        model.addAttribute("title", title);

        model.addAttribute("reportSearchInput", new ReportSearchInput());
        model.addAttribute("error", model.getAttribute("error"));

        //追記*****************************************************

        //ログイン中のユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        User member = userService.getUserByEmployeeCode(employeeCode);
        model.addAttribute("member", member);
        //報告一覧表示---------------------------------
        List<Report> reports = reportService.getfindAll(employeeCode);
//        model.addAttribute("reports", reports);

        //検索機能---------------------------------------
        //既読or未読
        for (Report report : reports) {
            boolean isFeedbackGiven = feedbackService.count(report.getId());
            report.setReadStatus(isFeedbackGiven ? "既読" : "未読");
        }
        model.addAttribute("reports", reports);
        //年月で重複しないList作成
        List<LocalDate> dateList = reports.stream()
                .map(Report::getDate)
                .map(date -> date.withDayOfMonth(1))
                .distinct()
                .toList();
        model.addAttribute("dateList", dateList);
        //データ格納用
        model.addAttribute("reportSortInput", new ReportSortInput());
        //追記*****************************************************

        return "member/report-search";
    }

    @PostMapping("/search-report")
    public String searchReport(
            ReportSearchInput reportSearchInput,
            RedirectAttributes redirectAttributes,
            ReportSortInput reportSortInput,
            Model model
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        String reportId = reportService.searchId(
                employeeCode,
                reportSearchInput.getDate()
        );

//追記*****************************************************
        //日付、、
        if (reportSortInput.getSort() == true) {
            reportSortInput.setEmployeeCode(employeeCode);

            //ソート用
            List<Report> reports = reportService.getSorrtReport(reportSortInput);
            User member = userService.getUserByEmployeeCode(employeeCode);
            for (Report report : reports) {
                boolean isFeedbackGiven = feedbackService.count(report.getId());
                report.setReadStatus(isFeedbackGiven ? "既読" : "未読");
            }
            model.addAttribute("member", member);
            model.addAttribute("reportSearchInput", new ReportSearchInput());
            model.addAttribute("error", model.getAttribute("error"));
            model.addAttribute("reports", reports);
            //年月で重複しないList作成
            List<LocalDate> dateList = reports.stream()
                    .map(Report::getDate)
                    .map(date -> date.withDayOfMonth(1))
                    .distinct()
                    .toList();
            model.addAttribute("dateList", dateList);
            //データ格納用
            model.addAttribute("reportSortInput", new ReportSortInput());
            return "member/report-search";
        }
//追記*****************************************************


        if (reportId == null) {
            redirectAttributes.addFlashAttribute("error", "選択された日付に提出されたレポートはありません");
            return "redirect:/member/report-search";
        }

        redirectAttributes.addAttribute("reportId", reportId);
        return "redirect:/member/reports/{reportId}";
    }

    @GetMapping("/reports/{reportId}")
    public String displayReportDetail(@PathVariable("reportId") int reportId, FeedbackUpdateInput feedbackUpdateInput, Model model, Boolean del) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Report report = reportService.getReportById(reportId);
//        if (report.getEmployeeCode() != employeeCode) {
//            return "redirect:/member/report/create";
//        }

        //Feedback feedback = feedbackService.getFeedbackById(reportId);
        //Assignment assignment = assignmentService.getAssignmentByEmployeeCode(employeeCode);

        List<TaskLog> taskLogs = taskLogService.getTaskLogsByReportId(reportId);
        User member = userService.getUserByEmployeeCode(report.getEmployeeCode());

        model.addAttribute("report", report);
        model.addAttribute("taskLogs", taskLogs);
        model.addAttribute("member", member);
        //model.addAttribute("feedback", feedback);

        model.addAttribute("beforeReportId", reportService.getBeforeIdById(reportId));
        model.addAttribute("afterReportId", reportService.getAfterIdById(reportId));

        String date = report.getDate().format(DateTimeFormatter.ofPattern("yyyy年M月d日(E)", Locale.JAPANESE));
        model.addAttribute("date", date);

        //フィードバック用追記
        //レポートが所持しているemployeeCode
        int reportByEmployeeCode = report.getEmployeeCode();

        //レポートのemployeeCodeとログインユーザーのemployeeCodeが一致ならフィードバックを閲覧する
        //不一致なら
        //レポート持ち主のemployeecodeで検索して、ismanager falseのアサインメントがあるかどうかチェック→assignmentがあったらTrueなければfalse
        if (employeeCode == reportByEmployeeCode) {
            boolean isMgr = false;
            model.addAttribute("isManager", isMgr);
        } else {
            boolean isMgr = assignmentService.getCountIsManager(employeeCode, reportId);
            model.addAttribute("isManager", isMgr);
        }

        if (del != null && del) {
            feedbackService.deleteById(reportId);
        }

        if (feedbackUpdateInput.getComment() != null && !feedbackService.count(reportId)) {
            feedbackUpdateInput.setNameByEmployeeCode(employeeCode, userService);
            feedbackUpdateInput.setReportId(reportId);
            feedbackService.create(feedbackUpdateInput);
            model.addAttribute("feedback", feedbackUpdateInput);
        } else if (feedbackService.count(reportId)) {
            Feedback feedback = feedbackService.getFeedbackById(reportId);


            model.addAttribute("feedback", feedback);
        }

        return "member/report-detail";
    }

    @PostMapping("/reports/{reportId}/delete/list")
    @Transactional
    public String listTransitionAfterDeleteReport(
            @PathVariable int reportId
    ) {
        Report report = reportService.getReportById(reportId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        if (report.getEmployeeCode() != employeeCode) {
            return "redirect:/member/report/create";
        }
        this.feedbackService.deleteById(reportId);
        this.taskLogService.deleteByReportId(reportId);
        this.reportService.deleteById(reportId);


        return "redirect:/member/report-search";
    }

    @GetMapping("/reports/{reportId}/delete")
    @Transactional
    public String deleteReport(
            @PathVariable int reportId
    ) {
        Report report = reportService.getReportById(reportId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        if (report.getEmployeeCode() != employeeCode) {
            return "redirect:/member/report/create";
        }

        this.feedbackService.deleteById(reportId);
        this.taskLogService.deleteByReportId(reportId);
        this.reportService.deleteById(reportId);

        return "redirect:/member/report/delete-completed";
    }

    @GetMapping("/report/delete-completed")
    public String displayReportDeleteCompleted(
    ) {
        return "member/report-delete-completed";
    }

    @GetMapping("/reports/{reportId}/edit")
    public String displayReportEdit(
            Model model,
            @PathVariable int reportId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Report report = this.reportService.getReportById(reportId);

        String title = "報告編集";
        model.addAttribute("title", title);

        if (report.getEmployeeCode() != employeeCode) {
            return "redirect:/member/report/create";
        }

        //遅刻・早退　関係
        Setting setting = this.settingService.getSettingTime(employeeCode);
        SettingInput settingInput = new SettingInput();
        String reason = "";
        if (report.getStartTime().isAfter(setting.getStartTime()) && report.getEndTime().isBefore(setting.getEndTime())) {
            settingInput.setEmployment(true);
            reason = "※遅刻 及び 早退の理由を記入してください";
        } else if (report.getStartTime().isAfter(setting.getStartTime())) {
            settingInput.setEmployment(true);
            reason = "※遅刻の理由を記入してください";
        } else if (report.getEndTime().isBefore(setting.getEndTime())) {
            settingInput.setEmployment(true);
            reason = "※早退の理由を記入してください";
        }

        model.addAttribute("reason", reason);
        model.addAttribute("settingInput", settingInput);


        List<TaskLog> taskLogs = this.taskLogService.getTaskLogsByReportId(reportId);

        model.addAttribute("report", report);
        model.addAttribute("taskLogs", taskLogs);
        model.addAttribute("reportUpdateInput", new ReportUpdateInput());

        return "member/report-edit";

    }

    @Transactional
    @PostMapping("/report/update")
    public String updateReport(ReportUpdateInput reportUpdateInput, RedirectAttributes redirectAttributes, SettingInput settingInput, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Report report = this.reportService.getReportById(reportUpdateInput.getReportId());

        if (report.getEmployeeCode() != employeeCode) {
            return "redirect:/member/report/create";
        }

        //遅刻・早退関係
        Setting setting = settingService.getSettingTime(employeeCode);
        String reason = "";
        if (reportUpdateInput.getStartTime().isAfter(setting.getStartTime()) && reportUpdateInput.getEndTime().isBefore(setting.getEndTime())) {
            reportUpdateInput.setIsLateness(true);
            reportUpdateInput.setIsLeftEarly(true);
            reason = "※遅刻 及び 早退の理由を記入してください。";
        } else if (reportUpdateInput.getStartTime().isAfter(setting.getStartTime())) {
            reportUpdateInput.setIsLateness(true);
            reason = "※遅刻の理由を記入してください";
        } else if (reportUpdateInput.getEndTime().isBefore(setting.getEndTime())) {
            reportUpdateInput.setIsLeftEarly(true);
            reason = "※早退の理由を記入してください";
        } else {
            reportUpdateInput.setLatenessReason(null);
        }

        if (reportUpdateInput.getIsLeftEarly() || reportUpdateInput.getIsLateness()) {
            if (reportUpdateInput.getLatenessReason() == null) {
                report.setStartTime(settingInput.getStartTime());
                report.setEndTime(settingInput.getEndTime());
                settingInput.setEmployment(true);
                model.addAttribute("reason", reason);
                model.addAttribute("reportUpdateInput", reportUpdateInput);
                model.addAttribute("settingInput", settingInput);
                String title = "報告編集";
                model.addAttribute("title", title);
                model.addAttribute("report", report);
                List<TaskLog> taskLogs = this.taskLogService.getTaskLogsByReportId(reportUpdateInput.getReportId());
                model.addAttribute("taskLogs", taskLogs);
                return "member/report-edit";
            }
        }

        this.reportService.update(reportUpdateInput);
        this.taskLogService.deleteByReportId(reportUpdateInput.getReportId());

        if (reportUpdateInput.getTaskLogs() != null) {
            List<TaskLog> taskLogs = reportUpdateInput.getTaskLogs();
            taskLogs.forEach(x -> x.setReportId(reportUpdateInput.getReportId()));
            for (TaskLog taskLog : taskLogs) {
                if (taskLog != null && taskLog.getName() != null) {
                    taskLogService.create(taskLog);
                }
            }
        }

        redirectAttributes.addAttribute("reportId", reportUpdateInput.getReportId());
        redirectAttributes.addFlashAttribute("editCompleteMSG", "報告を編集しました。");
        return "redirect:/member/reports/{reportId}";

    }

    @GetMapping("/user-main")
    public ModelAndView userMain(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        List<Assignment> myast = assignmentService.getAssignmentByEmployeeCode(employeeCode);
        if (myast == null) {
            myast = new ArrayList<>();
        }

        List<Assignment> allast = assignmentService.getAllAssignment();

        List<Team> allteam = teamService.getAllTeam();
        List<Team> myteammem = new ArrayList<>();
        List<Team> myteammgr = new ArrayList<>();

        List<User> allusers = userService.getAllEmployeeInfo();
        List<User> managers = new ArrayList<>();

        String title = "メイン";
        mav.addObject("title", title);

//        自分がメンバーとして所属しているチーム情報を自分のassignment情報から割り出す
        if (!myast.isEmpty()) {
            for (Team team : allteam) {
                for (Assignment ast : myast) {
                    if (team.getTeamId() == ast.getTeamId() && !ast.getIsManager()) {
                        myteammem.add(team);
                    } else if (team.getTeamId() == ast.getTeamId() && ast.getIsManager()) {
                        myteammgr.add(team);
                    }
                }
            }
        }

//        自分が所属しているチームのマネージャー情報を割り出す、自分がマネージャーだったらリストには追加しない
        for (Assignment ast : allast) {
            for (Team team : myteammem) {
                if (ast.getTeamId() == team.getTeamId()) {
                    for (User user : allusers) {
                        if (ast.getEmployeeCode() == user.getEmployeeCode() && user.getEmployeeCode() != employeeCode) {
                            if (ast.getIsManager()) {
                                managers.add(user);
                            }
                        }
                    }
                }
            }
        }

//        直近のレポート特定と未達成タスクリストの取得
        List<Report> two = reportService.getLastTwoByUser(employeeCode);
        Report lastReport = new Report();
        LocalDate todaysDate = LocalDate.now();

        if (two != null) {
            for (Report rp : two) {
                if (rp.getDate().isBefore(todaysDate)) {
                    lastReport = rp;
                    break;
                }
            }
        }

        List<TaskLog> taskLogs;
        if (two != null) {
            taskLogs = taskLogService.getIncompleteTaskLogsByReportId(lastReport.getId());
        } else {
            taskLogs = new ArrayList<>();
        }
        mav.addObject("taskList", taskLogs);

        mav.addObject("lastReport", lastReport);
        mav.addObject("managerList", managers);
        mav.addObject("assignmentList", myast);
        mav.addObject("memteamList", myteammem);
        mav.addObject("mgrteamList", myteammgr);

//        自分がマネージャーとして所属しているチームのメンバー抽出
        List<Assignment> asMgr = assignmentService.getAsManager(employeeCode);
        List<User> members = new ArrayList<>();
        if (!asMgr.isEmpty()) {
            for (Team tm : myteammgr) {
                for (Assignment as : allast) {
                    for (User us : allusers) {
                        if (tm.getTeamId() == as.getTeamId() && !as.getIsManager() && as.getEmployeeCode() == us.getEmployeeCode()) {
                            members.add(us);
                        }
                    }
                }
            }
        }

//        昨日の曜日を定義。昨日が日曜日か土曜日の場合は金曜日の日付を設定
        LocalDate yesterdayDate = todaysDate.minusDays(1);
        DayOfWeek dw = yesterdayDate.getDayOfWeek();
        if (dw.getValue() == 7) {
            yesterdayDate.minusDays(3);
        } else if (dw.getValue() == 6) {
            yesterdayDate.minusDays(2);
        }

//        今日報告提出したメンバー抽出
        List<User> todaymem = new ArrayList<>();

        if (!members.isEmpty()) {
            for (User user : members) {
                Report report = reportService.getOneByUserByDate(user.getEmployeeCode(), todaysDate);
                if (report != null) {
                    todaymem.add(user);
                }
            }
        }

//        前営業日に未提出のメンバー抽出
        List<User> notsubmem = new ArrayList<>();
        if (!members.isEmpty()) {
            for (User user : members) {
                Report report = reportService.getOneByUserByDate(user.getEmployeeCode(), yesterdayDate);
                if (report == null) {
                    notsubmem.add(user);

                }
            }
        }

        mav.addObject("todaymembers", todaymem);
        mav.addObject("notsubmit", notsubmem);

        mav.setViewName("member/user-main");

        return mav;
    }

    //ユーザー情報変更一覧
    @GetMapping("/userDetailsList")
    public String displayUserInfoList() {
        return "member/userDetailsList";
    }

    //ユーザー情報変更画面(名前、パスワード、アイコン)
    @GetMapping("/userDetailsList/userEdit")
    public String userEdit(Model model) {
        model.addAttribute("userEditInput", new UserEditInput());
        return "member/userDetailsList-userEdit";
    }

    //ユーザ情報編集情報処理
    @PostMapping("/userDetailsList/complete")
    public String editComplete(@ModelAttribute("userEditInput") UserEditInput userEditInput,
                               RedirectAttributes redirectAttributes) {
        //↓ログイン中のemployeeCodeをAuthentication(認証情報)から取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        userEditInput.setEmployeeCode(employeeCode);
        userEditInput.setRole("USER");

        //↓userSeriviceでの処理した値が正しくDBに"入ったら"ErrorMSGがnullになる
        Exception Error = userService.checkTest(userEditInput, employeeCode);

        if (Error != null) {
            redirectAttributes.addFlashAttribute("ErrorMSG", "更新失敗,再度お試しください");
            return "redirect:/member/userDetailsList-userEdit";
        }
        redirectAttributes.addFlashAttribute("editCompleteMSG", "情報を更新しました");
        return "redirect:/member/userDetailsList";
    }

//以下メールを送る記述
//    @Autowired
//    private MailSender sender;
//
//    @GetMapping("/mail")
//    public String nandemoii() {
//
//        SimpleMailMessage msg = new SimpleMailMessage();
//        //↓ここに送信先のメールを記述
//        msg.setTo("hiroi9361@gmail.com");
//        msg.setSubject("テストメール");//メールタイトル
//        msg.setText("Spring Boot より本文送信"); //本文の設定
//        //メールを送る
//        this.sender.send(msg);
//
//        return "member/userDetailsList";
//    }


/*以下メールを送る記述すごいやつバージョン
    上記のコメントアウトしてるやつは文章しか送れませんが以下の記述はhtmlや添付ファイルも送れます(MailServiceクラスに記述)
 */
    @GetMapping("/mail")
    public String sendMail() throws MessagingException, jakarta.mail.MessagingException {
        mailService.sendMail();
        return "member/userDetailsList";
    }
}

