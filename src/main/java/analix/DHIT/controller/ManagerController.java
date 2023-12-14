package analix.DHIT.controller;

import analix.DHIT.input.*;
import analix.DHIT.model.*;
import analix.DHIT.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final UserService userService;
    private final ReportService reportService;
    private final TaskLogService taskLogService;
    private final TeamService teamService;
    private final AssignmentService assignmentService;
    private final FeedbackService feedbackService;

    public ManagerController(
            UserService userservice,
            ReportService reportService,
            TaskLogService taskLogService,
            TeamService teamService,
            AssignmentService assignmentService,
            FeedbackService feedbackService) {
        this.userService = userservice;
        this.reportService = reportService;
        this.taskLogService = taskLogService;
        this.teamService = teamService;
        this.assignmentService = assignmentService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/home/{teamId}")
    public String displayHome(Model model,@PathVariable int teamId,
            @RequestParam(name = "searchCharacters", required = false) String searchCharacters
    ) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年M月d日(E)", Locale.JAPANESE));
        model.addAttribute("today", today);

        Team team = teamService.getTeamById(teamId);

        model.addAttribute("team", team);

//        チームメンバーと自分以外のマネージャー特定
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        List<Assignment> allast = assignmentService.getAssignmentByTeam(teamId);
        List<User> alluser = userService.getAllEmployeeInfo();
        List<User> members = new ArrayList<>();
        List<User> mgrs = new ArrayList<>();
        for(Assignment ast : allast){
            for(User usr : alluser){
                if(ast.getEmployeeCode() == usr.getEmployeeCode() && !ast.getIsManager()){
                    members.add(usr);
                }else if (ast.getEmployeeCode() == usr.getEmployeeCode() && ast.getIsManager()){
                    mgrs.add(usr);
                }
            }
        }
        //test***********
        AssignmentCreateInput assignmentCreateInput = new AssignmentCreateInput();

        assignmentCreateInput.setTeamId(teamId);
        assignmentCreateInput.setIsManager(assignmentService.getIsManager(employeeCode, teamId));
        model.addAttribute("assignment",assignmentCreateInput);
        //test***********
        model.addAttribute("managers", mgrs);

        String title = "メンバー一覧";
        model.addAttribute("title", title);

        //アイコン探し
        if (searchCharacters == null) {
            model.addAttribute("members", members);
//            model.addAttribute("members", userService.getAllMember());
            model.addAttribute("memberSearchInput", new MemberSearchInput());
            return "manager/home";
        }

        model.addAttribute("members", model.getAttribute("members"));
        model.addAttribute("memberSearchInput", new MemberSearchInput().withSearchCharacters(searchCharacters));
        //test***********
         model.addAttribute("assignment",assignmentCreateInput);
        //test***********
        return "manager/home";
    }

    @PostMapping("/search-member")
    public String searchMember(
            MemberSearchInput memberSearchInput,
            RedirectAttributes redirectAttributes
    ) {
        List<User> members = userService.getMemberBySearchCharacters(memberSearchInput.getSearchCharacters());
        redirectAttributes.addFlashAttribute("members", members);
        redirectAttributes.addAttribute("searchCharacters", memberSearchInput.getSearchCharacters());

        return "redirect:/manager/home";

    }

    @GetMapping("/report-search")
    public String displayReportSearch(
            @RequestParam(name = "employeeCode", required = true) int employeeCode,
            @ModelAttribute("assignment") AssignmentCreateInput assignmentCreateInput,
            Model model
    ) {
        String title = "報告一覧";
        model.addAttribute("title", title);

        //社員コードを元に対応するユーザー情報を取得
        User member = userService.getUserByEmployeeCode(employeeCode);

        //モデルに必要な情報を追加
        model.addAttribute("member", member);
        model.addAttribute("reportSearchInput", new ReportSearchInput());
        model.addAttribute("error", model.getAttribute("error"));

        //報告一覧表示---------------------------------
        List<Report> reports = reportService.getfindAll(employeeCode);
        //既読or未読
        for(Report report : reports){
            boolean isFeedbackGiven = feedbackService.count(report.getId());
            report.setReadStatus(isFeedbackGiven ? "既読" : "未読");
        }
        model.addAttribute("reports", reports);
        //検索機能---------------------------------------
        //年月で重複しないList作成
        List<LocalDate> dateList = reports.stream()
                .map(Report::getDate)
                .map(date -> date.withDayOfMonth(1))
                .distinct()
                .toList();
        model.addAttribute("dateList",dateList);
        //データ格納用
        model.addAttribute("reportSortInput",new ReportSortInput());


        //test---------------------------------
        model.addAttribute("assignment", assignmentCreateInput);
        //test---------------------------------

        return "manager/report-search";
    }

    @PostMapping("/search-report")
    //社員コードと日付を元に報告IDを検索
    public String searchReport(
            ReportSearchInput reportSearchInput,
            RedirectAttributes redirectAttributes,
            ReportSortInput reportSortInput,
            AssignmentCreateInput assignmentCreateInput,//test
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        String reportId = reportService.searchId(
                reportSearchInput.getEmployeeCode(),
                reportSearchInput.getDate()
        );

        //検索結果がない場合
        if (reportId == null) {
            redirectAttributes.addFlashAttribute("error", "ヒットしませんでした");
            return "redirect:/manager/report-search?employeeCode=" + reportSearchInput.getEmployeeCode();
        }

        //検索結果がある場合、Detailにリダイレクト
        redirectAttributes.addAttribute("reportId", reportId);

        //追記*****************************************************
        if(reportSortInput.getSort() == true) {
            reportSortInput.setEmployeeCode(employeeCode);

            //ソート用
            List<Report> reports = reportService.getSorrtReport(reportSortInput);

            model.addAttribute("reportSearchInput", new ReportSearchInput());
            model.addAttribute("error", model.getAttribute("error"));
            model.addAttribute("reports", reports);
            //年月で重複しないList作成
            List<LocalDate> dateList = reports.stream()
                    .map(Report::getDate)
                    .map(date -> date.withDayOfMonth(1))
                    .distinct()
                    .toList();
            model.addAttribute("dateList",dateList);
            //データ格納用
            model.addAttribute("reportSortInput",new ReportSortInput());
            return "member/report-search";
        }
//追記*****************************************************

        //検索結果がない場合
        if (reportId == null) {
            redirectAttributes.addFlashAttribute("error", "ヒットしませんでした");
            return "redirect:/manager/report-search?employeeCode=" + reportSearchInput.getEmployeeCode();
        }

        //検索結果がある場合、Detailにリダイレクト
        redirectAttributes.addAttribute("reportId", reportId);
        return "redirect:/manager/reports/{reportId}";

    }

    @GetMapping("/reports/{reportId}")
    public String displayReportDetail(@PathVariable("reportId") int reportId, FeedbackUpdateInput feedbackUpdateInput, Model model,Boolean del) {

        //test-------------------------
        //model.addAttribute("test",isManager);
        //test-------------------------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());


        if(del != null && del){
            feedbackService.deleteById(reportId);
        }

        if(feedbackUpdateInput.getComment() != null && !feedbackService.count(reportId)) {
            feedbackUpdateInput.setNameByEmployeeCode(employeeCode, userService);
            feedbackUpdateInput.setReportId(reportId);
            feedbackService.create(feedbackUpdateInput);
            model.addAttribute("feedback",feedbackUpdateInput);
        } else if (feedbackService.count(reportId)) {
            Feedback feedback = feedbackService.getFeedbackById(reportId);


            model.addAttribute("feedback",feedback);
        }


        String title = "報告詳細";
        model.addAttribute("title", title);

        Report report = reportService.getReportById(reportId);
        List<TaskLog> taskLogs = taskLogService.getTaskLogsByReportId(reportId);
        User member = userService.getUserByEmployeeCode(report.getEmployeeCode());

        model.addAttribute("report", report);
        model.addAttribute("taskLogs", taskLogs);
        model.addAttribute("member", member);


        model.addAttribute("beforeReportId", reportService.getBeforeIdById(reportId));
        model.addAttribute("afterReportId", reportService.getAfterIdById(reportId));

        String date = report.getDate().format(DateTimeFormatter.ofPattern("yyyy年M月d日(E)", Locale.JAPANESE));
        model.addAttribute("date", date);

        //test------------------------------------
        boolean isMgr = assignmentService.getCountIsManager(employeeCode,reportId);
        model.addAttribute("isManager",isMgr);
        //test------------------------------------
        return "member/report-detail";
//        return "manager/report-detail";
    }


    @GetMapping("/create")
    public String display(Model model) {

        String title = "ユーザー作成";
        model.addAttribute("title", title);
        List<Team> teamList = teamService.getAllTeam();
        List<AssignmentCreateInput> astList = new ArrayList<>();
        model.addAttribute("teamList", teamList);
        model.addAttribute("userCreateInput", new UserCreateInput());
        model.addAttribute("assignmentCreateInput", new AssignmentCreateInput());

        //test=-----------------------------------
        //AssignmentCreateInput assignmentCreateInput = new AssignmentCreateInput();

        //test=-----------------------------------


        return "manager/create";
    }

    //↓新規社員情報入力処理
    @PostMapping("/createEmployee")
    public String NewUserRegistrationInformation(@ModelAttribute("userCreateInput") UserCreateInput userCreateInput, @ModelAttribute("assignmentCreateInput") AssignmentCreateInput assignmentCreateInput,RedirectAttributes redirectAttributes){

        Integer employeeCode = userService.checkDuplicates(userCreateInput.getEmployeeCode());
        if (employeeCode != null) {
            //employeeCodeが重複してるため、画面リダイレクトでerrorを表示
            redirectAttributes.addFlashAttribute("EmployeeCodeError", "社員番号が重複しています");
            return "redirect:/manager/create";
        }
        userService.encodePassword(userCreateInput);
        //もしアイコン&パスワードが正常にDBに処理できなかったらリダイレクトerror
        try {
            userService.base64Converter(userCreateInput);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("EncodeError","エラーが出ました");
            return "redirect:/manager/create";
        }
        //inputデータをDBへ
        userService.createEmployeeInformation(userCreateInput);

//        if (AssignmentCreateInput.getAssignments() != null) {
//            List<Assignment> assignments = AssignmentCreateInput.getAssignments();
//            assignments.forEach(x -> x.setEmployeeCode(userCreateInput.getEmployeeCode()));
//            for (Assignment assignment : assignments) {
//                if (assignment != null && !assignmentService.existsAssignment(assignment.getEmployeeCode(), assignment.getTeamId())) {
//                    assignmentService.create(assignment);
//                }else{
//                    redirectAttributes.addFlashAttribute("EncodeError","チーム登録が既に存在しています");
//                    return "redirect:/manager/create";
//                }
//            }
//        }

        if (assignmentCreateInput.getTeamId() != 0){

            int newAssignmentId = assignmentService.create(
                    assignmentCreateInput.getEmployeeCode(),
                    assignmentCreateInput.getIsManager(),
                    assignmentCreateInput.getTeamId()
            );
        }

        return "redirect:/manager/employeeList";
    }

    @GetMapping("/employeeList")
    public String displayEmployeeList(Model model) {
        List<User> userList = userService.getAllEmployeeInfo();
        String title = "ユーザー一覧";
        model.addAttribute("title", title);

        model.addAttribute("userList", userList);
        return "manager/employeeList";
    }
    //社員削除画面表示
    @Transactional
    @PostMapping("employeeList-deleteUser")
    public String displayDeleteUser(@RequestParam("employeeCode") int employeeCode,
                                    @RequestParam("name") String name,
                                    RedirectAttributes attributes,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        // ログイン中のユーザー情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // ログイン中のユーザーのemployeeCodeを取得
        String loggedEmployeeCode = auth.getName();
        // ユーザーが削除しようとしているemployeeCodeとログイン中のemployeeCodeを比較
        if (loggedEmployeeCode.equals(String.valueOf(employeeCode))) {
            redirectAttributes.addFlashAttribute("errorEmployeeMsg", "ログイン中のユーザーの編集・削除は出来ません");
            return "redirect:/manager/employeeList";
        }

        List<Integer> reportIdAllIdGet = reportService.getIdsByEmployeeCode(employeeCode);

        for (Integer id : reportIdAllIdGet) {
            feedbackService.deleteById(id);
            taskLogService.deleteByReportId(id);
            reportService.deleteById(id);
        }
        assignmentService.deleteByUser(employeeCode);

        userService.deleteById(employeeCode);
        redirectAttributes.addFlashAttribute("deleteCompleteMSG", name + "を削除しました");
        return "redirect:/manager/employeeList";

//        model.addAttribute("name", name);
//        model.addAttribute("employeeCode", employeeCode);
//        return "manager/employeeList-deleteUser";
    }


    //削除完了画面表示及び、削除処理
    @PostMapping("employeeList-deleteComplete")
    @Transactional
    public String deletionProcess(@RequestParam("employeeCode") int employeeCode,
                                  @RequestParam("name") String name,
                                  RedirectAttributes redirectAttributes) {
        //reportテーブルのemployeeCodeに紐づいているidを全取得
        List<Integer> reportIdAllIdGet = reportService.getIdsByEmployeeCode(employeeCode);
        //task_logのreport_idを削除
        //reportテーブルのemployeeCodeに紐づいているidを全削除
        for (Integer id : reportIdAllIdGet) {
            taskLogService.deleteByReportId(id);
            reportService.deleteById(id);
        }
        assignmentService.deleteByUser(employeeCode);
        //userテーブルの値を全部削除
        userService.deleteById(employeeCode);
        redirectAttributes.addFlashAttribute("deleteCompleteMSG", name + "を削除しました");
        return "redirect:/manager/employeeList";
    }

    //編集画面表示
    @GetMapping("employeeList-edit")
    public String editDisplayMenu(@RequestParam("employeeCode") int employeeCode,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedEmployeeCode = auth.getName();

        String title = "ユーザー編集";
        model.addAttribute("title", title);

        if (loggedEmployeeCode.equals(String.valueOf(employeeCode))) {
            redirectAttributes.addFlashAttribute("errorEmployeeMsg", "ログイン中のユーザーの編集・削除は出来ません");
            return "redirect:/manager/employeeList";
        }
        model.addAttribute("userEditInput", new UserEditInput());
        model.addAttribute("employeeCode", employeeCode);

        return "manager/employeeList-edit";
    }
    //編集画面処理
    @PostMapping("editEmployeeComplete")
    public String editingProcess(@ModelAttribute("userEditInput") UserEditInput userEditInput,
                                 @RequestParam("employeeCode") int employeeCode,
                                 RedirectAttributes redirectAttributes) {
        Exception ErrorMSG = userService.checkTest(userEditInput, employeeCode);
        if (ErrorMSG != null) {
            redirectAttributes.addFlashAttribute("EncodeError", "更新失敗:imageファイル以外送らないでください");
            redirectAttributes.addAttribute("employeeCode", employeeCode);
            return "redirect:/manager/employeeList-edit";
        }
        redirectAttributes.addFlashAttribute("editCompleteMSG", "社員番号:" + employeeCode + "の情報を更新しました");
        redirectAttributes.addAttribute("employeeCode", employeeCode);
        return "redirect:/manager/employeeList";
    }


    @GetMapping("/teamlist")
    public String displayTeamList(Model model){
        List<Team> teamList = teamService.getAllTeam();
        model.addAttribute("teamList", teamList);
        String title = "チーム一覧";
        model.addAttribute("title", title);

        return "manager/teamlist";
    }

    @GetMapping("/team-create")
    public String displayTeamCreate(Model model){
        String title = "チーム作成";
        model.addAttribute("title", title);
        model.addAttribute("teamCreateInput", new TeamCreateInput());
        return "manager/team-create";
    }

    @Transactional
    @PostMapping("/team-create")
    public String createTeam(TeamCreateInput teamCreateInput, RedirectAttributes redirectAttributes){

        int newTeamId = teamService.create(
                teamCreateInput.getName()
        );

        return "redirect:/manager/teamlist";

    }

    @GetMapping("/teams/{teamId}/edit")
    public String displayTeamEdit(Model model, @PathVariable int teamId){

        Team team = this.teamService.getTeamById(teamId);

        String title = "チーム編集";
        model.addAttribute("title", title);

        model.addAttribute("team", team);
        model.addAttribute("teamUpdateInput", new TeamUpdateInput());

        return "manager/team-edit";
    }

    @Transactional
    @PostMapping("/team/update")
    public String updateTeam(TeamUpdateInput teamUpdateInput, RedirectAttributes redirectAttributes){

        Team team = this.teamService.getTeamById(teamUpdateInput.getTeamId());
        this.teamService.update(teamUpdateInput);

        redirectAttributes.addAttribute("teamId", teamUpdateInput.getTeamId());
        return "redirect:/manager/teamlist";
    }

    @GetMapping("/teams/{teamId}/detail")
    public String displayTeamDetail(Model model, @PathVariable int teamId){

        String title = "チーム詳細";
        model.addAttribute("title", title);

        Team team = this.teamService.getTeamById(teamId);

        List<Assignment> assignments = assignmentService.getAssignmentByTeam(teamId);

        List<User> managers = new ArrayList<User>();
        List<User> members = new ArrayList<User>();
        for(Assignment ast : assignments){
            if(ast.getIsManager()){
                managers.add(userService.getUserByEmployeeCode(ast.getEmployeeCode()));
            }else{
                members.add(userService.getUserByEmployeeCode(ast.getEmployeeCode()));
            }
        }

        model.addAttribute("team", team);
        model.addAttribute("managers", managers);
        model.addAttribute("members", members);

        return "manager/team-detail";
    }

    @GetMapping("/assignment/{teamId}")
    public String createAssignment(Model model, @PathVariable int teamId){

        String title = "配属作成";
        model.addAttribute("title", title);

        Team team = this.teamService.getTeamById(teamId);
        List<User> users = userService.getAllEmployeeInfo();

        model.addAttribute("team", team);
        model.addAttribute("users", users);
        model.addAttribute("assignmentCreateInput", new AssignmentCreateInput());

        return "manager/assignment-create";
    }

    @Transactional
    @PostMapping("/assignment/create")
    public String creatingAssignment(AssignmentCreateInput assignmentCreateInput, RedirectAttributes redirectAttributes){

        if (assignmentCreateInput.getTeamId() != 0 && !assignmentService.existsAssignment(assignmentCreateInput.getEmployeeCode(), assignmentCreateInput.getTeamId())) {

            int newAssignmentId = assignmentService.create(
                    assignmentCreateInput.getEmployeeCode(),
                    assignmentCreateInput.getIsManager(),
                    assignmentCreateInput.getTeamId()
            );
        }else{
            redirectAttributes.addFlashAttribute("errorAstMsg", "該当のユーザーはすでに追加されています");
        }

        int teamId = assignmentCreateInput.getTeamId();
        redirectAttributes.addAttribute("teamId", teamId);

        return "redirect:/manager/teams/{teamId}/detail";
    }

    @Transactional
    @PostMapping("/assignment/{teamId}/{employeeCode}/delete")
    public String deleteAst(@PathVariable int teamId, @PathVariable int employeeCode){
        List<Assignment> assignments = assignmentService.getAssignmentByTeam(teamId);
        Assignment ast = new Assignment();

        for (Assignment as : assignments){
            if (as.getEmployeeCode() == employeeCode){
                ast = as;
            }
        }

        assignmentService.deleteById(ast.getAssignmentId());

        return "redirect:/manager/teams/{teamId}/detail";
    }


    @PostMapping("/teams/{teamId}/delete")
    @Transactional
    public String deleteTeam(
            @PathVariable int teamId
    ) {
        Team team = teamService.getTeamById(teamId);

        this.assignmentService.deleteByTeam(teamId);
        this.teamService.deleteById(teamId);

        return "redirect:/manager/teamlist";
    }


    @PostMapping("/search-employeeList")
    public String searchEmployeeList(
            MemberSearchInput memberSearchInput,
            RedirectAttributes redirectAttributes
    ) {
        List<User> members = userService.getMemberBySearchCharacters(memberSearchInput.getSearchCharacters());
        redirectAttributes.addFlashAttribute("members", members);
        redirectAttributes.addAttribute("searchCharacters", memberSearchInput.getSearchCharacters());

        return "redirect:/manager/home";

    }

//    ////////// 2023/12/14 富山 START //////////

    @GetMapping("/employeeList")
    public String showEmployeeList(Model model) {
        List<User> userList = userService.getAllEmployeeInfo();
        model.addAttribute("userList", userList);
        model.addAttribute("searchForm", new SearchForm());

        return "employeeList";
    }

    @PostMapping("/employeeList")
    public String searchEmployeeList(@ModelAttribute("SearchForm") SearchForm searchForm, Model model) {
        List<User> userList = employeeService.searchEmployees(searchForm.getSearchType(), searchForm.getSearchInput());
        model.addAttribute("userList", userList);

        return "employeeList";
    }


//    ////////// 2023/12/14 富山 END //////////

}
