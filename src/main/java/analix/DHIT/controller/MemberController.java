package analix.DHIT.controller;


import analix.DHIT.config.LoginUserDetailsService;
import analix.DHIT.input.*;
import analix.DHIT.model.*;
import analix.DHIT.service.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final   MailService mailService;
    private final   ApplyService applyService;


//    @Autowired
    public MemberController(UserService userService,
                            TaskLogService taskLogService,
                            ReportService reportService,
                            FeedbackService feedbackService,
                            AssignmentService assignmentService,
                            TeamService teamService,
                            SettingService settingService,
                            MailService mailService,
                            ApplyService applyService) {
        this.userService = userService;
        this.taskLogService = taskLogService;
        this.reportService = reportService;
        this.feedbackService = feedbackService;
        this.assignmentService = assignmentService;
        this.teamService = teamService;
        this.settingService = settingService;
        this.mailService = mailService;
        this.applyService = applyService;
    }

    @GetMapping("/report/create")
    public String displayReportCreate(
            Model model,
            LocalDate targetDate
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
        if (targetDate != null){
            reportCreateInput.setDate(targetDate);
        }
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

    ////削除予定
        //report_idを参照してtask_Logの値を取得しset
        //reportCreateInput.setTaskLogs(taskLogService.getIncompleteTaskLogsByReportId(Integer.parseInt(latestReportId)));
    ////削除予定
        //未達成のタスクを表示する
        reportCreateInput.setTaskLogs(taskLogService.selectByEmployeeCode(employeeCode));

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
                    List<TaskLog>taskList = this.taskLogService.taskListByName(taskLog.getName());
                    if (taskList.isEmpty()){
                        taskLog.setCounter(1);
                    } else {
                        taskLog.setCounter(taskList.size() + 1);
                        taskLog.setSorting(taskList.get(0).getSorting());
                    }
                    taskLog.setEmployeeCode(employeeCode);
                    if(taskLog.getCounter() == 1){
                        int maxNum = taskLogService.maxTask() + 1;
                        taskLog.setSorting(maxNum);
                    }
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
                boolean addMiddle = false;
                if (taskLog != null && taskLog.getName() != null) {
                    //taskLog.getName()でtask_logDBに検索をかける
                    boolean existingTask = this.taskLogService.countName(taskLog.getName());
                    //無い時
                    if(!existingTask){
                        taskLog.setCounter(taskLog.getCounter() + 1);
                        if(taskLog.getCounter() == 1){
                            int maxNum = taskLogService.maxTask() + 1;
                            taskLog.setSorting(maxNum);
                        }
                    //有る時
                    } else {
                        //taskLog.getName()とreportUpdateInput.getDate()を参照して
                        //時系列的に適切なcounterをセットし、以降のcounterを採番する
                        List<TaskLog>taskList = this.taskLogService.taskListByName(taskLog.getName());
                        LocalDate date = reportUpdateInput.getDate();
                        boolean once = false;
                        int count = 0;
                        int sort = 0;
                        int taskId =0;
                        for(TaskLog task : taskList){
                            Report reportDate = reportService.getReportById(task.getReportId());
                            if (date.isBefore(reportDate.getDate())){
                                addMiddle = true;
                                if (!once){
                                    count = task.getCounter();
                                    sort = task.getSorting();
                                    taskLog.setCounter(count);
                                    taskLog.setSorting(sort);
                                    count = taskLog.getCounter();
                                    //DB更新
                                    taskLogService.create(taskLog);
                                    count++;
                                    taskLog.setId(task.getId());
                                    taskLog.setCounter(count);
                                    taskLog.setSorting(sort);
                                    taskLogService.setCounter(taskLog);
                                    count++;
                                    once = true;
                                }else{
                                    taskLog.setId(task.getId());
                                    taskLog.setCounter(count);
                                    taskLog.setSorting(sort);
                                    count++;
                                    //DB更新
                                    taskLogService.setCounter(taskLog);
                                }
                            }
                        }
                        if (!once){
                            int taskIndex = taskList.size() - 1;
                            TaskLog newTask = taskIndex >= 0 ? taskList.get(taskIndex) : null;
                            taskLog.setCounter(newTask.getCounter()+1);
                            taskLog.setSorting(newTask.getSorting());
                        }
                    }
                    if (!addMiddle) {
                        taskLogService.create(taskLog);
                    }
                }
            }
        }

        redirectAttributes.addAttribute("reportId", reportUpdateInput.getReportId());
        redirectAttributes.addFlashAttribute("editCompleteMSG", "報告を編集しました。");
        return "redirect:/member/reports/{reportId}";

    }

    @GetMapping("/task-list")
    public String taskList( Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int myEmployeeCode = Integer.parseInt(authentication.getName());

        List<TaskLog> taskLogs = new ArrayList<>();
        taskLogs = this.taskLogService.taskList(myEmployeeCode);
        User memberName = userService.getUserByEmployeeCode(myEmployeeCode);
        String member = memberName.getName();
        boolean Search = false;
        boolean teamTask = false;
        model.addAttribute("taskList",taskLogs);
        model.addAttribute("member",member);
        model.addAttribute("TaskSearchInput",new TaskSearchInput());
        model.addAttribute("Search",Search);
        model.addAttribute("teamTask",teamTask);
        return "member/taskList";
    }

    @PostMapping("/task-list")
    public String taskSearchList(TaskSearchInput taskSearchInput, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int myEmployeeCode = Integer.parseInt(authentication.getName());
        taskSearchInput.setEmployeeCode(myEmployeeCode);
//        myEmployeeCodeで取得していたものをフィルター条件で取得する為に
//        ここをフィルターの条件でDBから持ってくる処理に変える
        List<TaskLog> taskLogs = new ArrayList<>();
        if(taskSearchInput.getState().isEmpty() && taskSearchInput.getProgressRateAbove()==0 && taskSearchInput.getProgressRateBelow()==0) {
            taskLogs = this.taskLogService.taskList(myEmployeeCode);
        }else{
            taskLogs = this.taskLogService.taskFilter(taskSearchInput);
        }
        model.addAttribute("taskList",taskLogs);
//        ここをフィルターの条件でDBから持ってくる処理に変える
        boolean Search = true;
        boolean teamTask = false;
        User memberName = userService.getUserByEmployeeCode(myEmployeeCode);
        String member = memberName.getName();
        model.addAttribute("member",member);
        model.addAttribute("TaskSearchInput",new TaskSearchInput());
        model.addAttribute("Search",Search);
        model.addAttribute("teamTask",teamTask);

        return "member/taskList";
    }

    @GetMapping("/taskMenu")
    public String getTestPage(Model model) {
        boolean fastContact = true;
        boolean secondContact = false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        ////employeeCodeからマネジャーの有無を取得し、
        boolean isManager = assignmentService.getCountIsManagerByEmployeeCode(employeeCode);
        ////fastContactで、マネジャーがあれば、チームメンバーのボタンを表示し、
        ////マネジャーが無ければ、自分のタスクを表示する⇒タスク一覧を押下した段階で、
        ////member/task-menuを開かずにtask-Listを表示させる
        if(!isManager){

           List<TaskLog> taskLogs = this.taskLogService.taskList(employeeCode);

            User memberName = userService.getUserByEmployeeCode(employeeCode);
            String member = memberName.getName();
            boolean Search = false;
            boolean teamTask = false;
            model.addAttribute("taskList",taskLogs);
            model.addAttribute("member",member);
            model.addAttribute("TaskSearchInput",new TaskSearchInput());
            model.addAttribute("Search",Search);
            model.addAttribute("teamTask",teamTask);
            return "member/taskList";
        }


        model.addAttribute("fastContact",fastContact);
        model.addAttribute("secondContact",secondContact);
        model.addAttribute("isManager",isManager);

        return "member/task-menu";
    }

    @GetMapping("taskSubMenu/{teamId}")
    public String postTestPagea(
            Model model,
            @PathVariable int teamId
    ){
        model.addAttribute("teamId",teamId);
        model.addAttribute("teamUpdateInput",new TeamUpdateInput());
        return"member/taskRedirect";
    }
    @PostMapping("/taskSubMenu")
    public String postTestPage(
            Model model,
            @RequestParam(value = "task", required = false) String task,
            @ModelAttribute("teamUpdateInput") TeamUpdateInput teamUpdateInput
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        boolean fastContact = false;
        boolean secondContact = false;
        boolean finalContact = false;
        List<TaskLog> taskLogs = new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<Assignment> assignments = new ArrayList<>();

        if(task != null) {
            switch (task) {
                case "1"://自分のタスク
                    taskLogs = this.taskLogService.taskList(employeeCode);

                    User memberName = userService.getUserByEmployeeCode(employeeCode);
                    String member = memberName.getName();
                    boolean Search = false;
                    boolean teamTask = false;
                    model.addAttribute("taskList",taskLogs);
                    model.addAttribute("member",member);
                    model.addAttribute("TaskSearchInput",new TaskSearchInput());
                    model.addAttribute("Search",Search);
                    model.addAttribute("teamTask",teamTask);
                    return "member/taskList";
                case "2"://チームメンバー
                    teams = this.teamService.selectTeamByEmployeeCode(employeeCode);
                    secondContact = true;
                    break;
            }
        }
        if (teamUpdateInput.getTeamId() != 0){
            finalContact = true;
            assignments = assignmentService.selectEmployeeCodeByTeamId(teamUpdateInput.getTeamId());

            for (Assignment assignment : assignments){
                List<TaskLog>memberTask = this.taskLogService.taskList(assignment.getEmployeeCode());
                User user = userService.selectUserById(assignment.getEmployeeCode());
                for (TaskLog taskLog : memberTask) {
                    taskLog.setUserName(user.getName());
                }
                taskLogs.addAll(memberTask);
            }
            //taskLogs = this.taskLogService.taskList(myEmployeeCode);
            String member = "チームメンバー";
            boolean Search = false;
            boolean teamTask = true;
            int teamId = teamUpdateInput.getTeamId();
            model.addAttribute("teamId",teamId);
            model.addAttribute("taskList",taskLogs);
            model.addAttribute("member",member);
            model.addAttribute("TaskSearchInput",new TaskSearchInput());
            model.addAttribute("Search",Search);
            model.addAttribute("teamTask",teamTask);
            return "member/taskList";
        }
        model.addAttribute("fastContact",fastContact);
        model.addAttribute("secondContact",secondContact);
        model.addAttribute("finalContact",finalContact);
        model.addAttribute("taskLogs", taskLogs);
        model.addAttribute("teams", teams);
        model.addAttribute("teamUpdateInput", new TeamUpdateInput());
        return "member/task-menu";
    }

    @GetMapping("/taskDetail/{sorting}")
    public String displayReportDetail(@PathVariable("sorting") int sorting,
                                      @RequestParam(value = "employeeCode", required = false) String employeeCode,
                                      @RequestParam(value = "teamId", required = false) String teamId,
                                      Model model) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        int employeeCode = Integer.parseInt(authentication.getName());

        List<TaskDetailInput> taskDetailInput = new ArrayList<>();
        taskDetailInput = this.taskLogService.taskDetail(sorting, Integer.parseInt(employeeCode));
        model.addAttribute("taskDetail",taskDetailInput);
        model.addAttribute("teamId",teamId);
        model.addAttribute("employeeCode",employeeCode);

        return "member/taskDetail";
    }

    @GetMapping("/user-main")
    public ModelAndView userMain(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        List<Assignment> myast = assignmentService.getAssignmentByEmployeeCode(employeeCode);
        if (myast == null) {
            myast = new ArrayList<>();
        }

        LocalDate targetDate;
        LocalDate firstDayOfLastWeek = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();

        if (currentDayOfWeek.equals(DayOfWeek.MONDAY) ||
                currentDayOfWeek.equals(DayOfWeek.SATURDAY) ||
                currentDayOfWeek.equals(DayOfWeek.SUNDAY)) {
            targetDate = firstDayOfLastWeek.plusDays(4);
        } else {
            targetDate = LocalDate.now().minusDays(1);
        }
        boolean hasSentReport = reportService.existsReport(employeeCode, targetDate);

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
        mav.addObject("targetDate", targetDate);
        mav.addObject("hasSentReport", hasSentReport);

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

    //報告未提出メンバーへ通知メールを送信する
    @GetMapping("/sendReportReminder")
    public ResponseEntity<String> sendReportReminder(@RequestParam("employeeCode") int employeeCode,
                                                     Model model) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");

        User member = userService.getUserByEmployeeCode(employeeCode);
        String memberEmail = member.getEmail();
        String memberName = member.getName();

        String subject = "【DHITシステム】報告提出の通知";
        String body = "昨日、報告未提出があります。\n" +
                "\n" +
                "下記より報告を行ってください。\n" +
                baseUrl + "/login\n" +
                "※当メールは送信専用となっております。";

        try {
            if (!memberEmail.isEmpty()) {
                mailService.sendMail(memberEmail, subject, body);
                return ResponseEntity.ok("{\"message\":\"" + memberName + "\"}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Member email is empty");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(memberName + "のメール送信に失敗しました" + ". Error: " + e.getMessage());
        }
    }


    /////////////////////////////////////////////////////////////////////////
    @GetMapping("/apply/create")
    public String displayApplyCreate(
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //バリデーションInteger
        int employeeCode = Integer.parseInt(authentication.getName());

        ApplyCreateInput applyCreateInput = new ApplyCreateInput();
        SettingInput settingInput = new SettingInput();
        //java.timeパッケージから現在の時刻を取得
        applyCreateInput.setCreatedDate(LocalDateTime.now());

        String title = "申請作成";
        model.addAttribute("title", title);
        //規定の終業時間を取得し、セット
        Setting setting = settingService.getSettingTime(employeeCode);
        settingInput.setStartTime(setting.getStartTime());
        settingInput.setEndTime(setting.getEndTime());
//        settingInput.setEmployment(false);
        model.addAttribute("settingInput", settingInput);

        // 提出ボタンを押した瞬間の時刻を取得し、createdDateにセット
        applyCreateInput.setCreatedDate(LocalDateTime.now());

        model.addAttribute("applyCreateInput", applyCreateInput);
        return "member/apply-create";

    }

    //↓Transactionalはトランザクション処理で一連の流れが失敗した場合ロールバックする
    @Transactional
    @PostMapping("/apply/create")
    public String createApply(ApplyCreateInput applyCreateInput, RedirectAttributes redirectAttributes, SettingInput settingInput, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Setting setting = settingService.getSettingTime(employeeCode);

        // 提出ボタンを押した瞬間の時刻を取得し、createdDateにセット
        applyCreateInput.setCreatedDate(LocalDateTime.now());

        //newApplyIdには新たにInsertされたreportのIDが入る
        int newApplyId = applyService.create(
                employeeCode,
                applyCreateInput.getApplicationType(),
                applyCreateInput.getAttendanceType(),
                applyCreateInput.getStartDate(),
                applyCreateInput.getEndDate(),
                applyCreateInput.getStartTime(),
                applyCreateInput.getEndTime(),
                applyCreateInput.getReason(),
                applyCreateInput.getApproval(),
                applyCreateInput.getCreatedDate()
        );

       return "redirect:/member/apply/create-completed";
    }

    // 申請提出完了画面
    @GetMapping("/apply/create-completed")
    public String displayApplyCreateCompleted(
    ) {
        return "member/apply-create-completed";
    }

    // 申請一覧
    @GetMapping("/apply-search")
    public String displayApplySearch(
            Model model
    ) {
        String title = "申請一覧";
        model.addAttribute("title", title);

        model.addAttribute("applySearchInput", new ApplySearchInput());
        model.addAttribute("error", model.getAttribute("error"));

        //ログイン中のユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        User member = userService.getUserByEmployeeCode(employeeCode);
        model.addAttribute("member", member);
        //報告一覧表示---------------------------------
        List<Apply> applys = applyService.getfindAll(employeeCode);

        //検索機能---------------------------------------

        //既読or未読
//        for (Apply apply : applys) {
//            boolean isApprovalGiven = applyService.count(apply.getId());
//            apply.setStatus(isApprovalGiven ? "既読" : "未読");
//        }
        model.addAttribute("applys", applys);
//        //年月で重複しないList作成
//        List<LocalDate> dateList = applys.stream()
//                .map(Apply::getDate)
//                .map(date -> date.withDayOfMonth(1))
//                .distinct()
//                .toList();
//        model.addAttribute("dateList", dateList);

//        //データ格納用
        model.addAttribute("applySortInput", new ApplySortInput());

        return "member/apply-search";
    }

    @PostMapping("/search-apply")
    public String searchApply(
            ApplySearchInput applySearchInput,
            RedirectAttributes redirectAttributes,
            ApplySortInput applySortInput,
            Model model
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        String applyId = applyService.searchId(
                employeeCode,
                applySearchInput.getCreatedDate()
        );

        //日付、、
        if (applySortInput.getSort()) {
            applySortInput.setEmployeeCode(employeeCode);

            //ソート用
            List<Apply> applys = applyService.getSortApply(applySortInput);
            User member = userService.getUserByEmployeeCode(employeeCode);

            model.addAttribute("member", member);
            model.addAttribute("applySearchInput", new ApplySearchInput());
            model.addAttribute("error", model.getAttribute("error"));
            model.addAttribute("applys", applys);


//            年月で重複しないList作成
//            List<LocalDateTime> dateList = applys.stream()
//                    .map(Apply::getCreatedDate)
//                    .map(date -> date.withDayOfMonth(1))
//                    .distinct()
//                    .toList();
//            model.addAttribute("dateList", dateList);

            //データ格納用
            model.addAttribute("applySortInput", new ApplySortInput());
            return "member/apply-search";
        }

        redirectAttributes.addAttribute("applyId", applyId);
        return "redirect:/member/applys/{applyId}";
    }

    @GetMapping("/apply/{applyId}")
    public String applyDetail(
            @PathVariable("applyId") int applyId,
            Model model
    ) {
        Apply apply = applyService.findById(applyId);
        model.addAttribute("apply",apply);
        return "/member/apply-detail";
    }
    @GetMapping("/apply/{applyId}/delete")
    @Transactional
    public String deleteApply(
            @PathVariable("applyId") int applyId,
            Model model
    ) {
        Apply apply = applyService.findById(applyId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        if (apply.getEmployeeCode() != employeeCode) {
            return "redirect:/member/apply-detail";
        }

        this.applyService.deleteById(applyId);

        User member = userService.getUserByEmployeeCode(employeeCode);
        model.addAttribute("member", member);
        List<Apply> applys = applyService.getfindAll(employeeCode);
        model.addAttribute("applys",applys);
        model.addAttribute("applySearchInput", new ApplySearchInput());
        model.addAttribute("error", model.getAttribute("error"));
        model.addAttribute("applySortInput", new ApplySortInput());
        String title = "申請一覧";
        model.addAttribute("title", title);

        return "member/apply-search";
    }

}


