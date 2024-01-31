package analix.DHIT.controller;

import analix.DHIT.input.*;
import analix.DHIT.model.*;
import analix.DHIT.repository.MysqlTeamRepository;
import analix.DHIT.repository.MysqlUserRepository;
import analix.DHIT.repository.UserRepository;
import analix.DHIT.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final UserService userService;
    private final ReportService reportService;
    private final TaskLogService taskLogService;
    private final TeamService teamService;
    private final AssignmentService assignmentService;
    private final FeedbackService feedbackService;
    private final SettingService settingService;
    private MysqlTeamRepository mysqlTeamRepository;

    public ManagerController(
            UserService userservice,
            ReportService reportService,
            TaskLogService taskLogService,
            TeamService teamService,
            AssignmentService assignmentService,
            FeedbackService feedbackService,
            SettingService settingService) {
        this.userService = userservice;
        this.reportService = reportService;
        this.taskLogService = taskLogService;
        this.teamService = teamService;
        this.assignmentService = assignmentService;
        this.feedbackService = feedbackService;
        this.settingService = settingService;
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

        for (User member : members) {
            List<Report> reports = reportService.getfindAll(member.getEmployeeCode());
            //既読or未読
            for(Report report : reports){
                boolean isFeedbackGiven = feedbackService.count(report.getId());
                report.setReadStatus(isFeedbackGiven ? "既読" : "未読");
                if(report.getReadStatus() == "未読") {
                    member.setReadReport(false);
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
            User member,
            Model model
    ) {

        String reportId = reportService.searchId(
                reportSearchInput.getEmployeeCode(),
                reportSearchInput.getDate()
        );


        //検索結果がある場合、Detailにリダイレクト
//        redirectAttributes.addAttribute("reportId", reportId);

        //追記*****************************************************
        if(reportSortInput.getSort() == true) {
            reportSortInput.setEmployeeCode(member.getEmployeeCode());

            //ソート用
            List<Report> reports = reportService.getSorrtReport(reportSortInput);
            member = userService.getUserByEmployeeCode(member.getEmployeeCode());
            for(Report report : reports){
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
            model.addAttribute("dateList",dateList);
            //データ格納用
            model.addAttribute("reportSortInput",new ReportSortInput());
            return "manager/report-search";
        }
//追記*****************************************************


        //検索結果がない場合
        if (reportId == null) {
            redirectAttributes.addFlashAttribute("error", "選択された日付に提出されたレポートはありません");
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
        //フィードバック者
        Feedback feedback = feedbackService.getFeedbackById(reportId);
        if (feedback != null){
            String reportName = feedback.getName();
            //閲覧者
            User user = userService.getUserByEmployeeCode(employeeCode);
            String myName = user.getName();
            boolean isFeedback = reportName.equals(myName);
            model.addAttribute("isFeedback",isFeedback);
        }else {
            boolean isFeedback = false;
            model.addAttribute("isFeedback",isFeedback);
        }
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

        User user = userService.getUserByEmployeeCode(userCreateInput.getEmployeeCode());
        String name = user.getName();
        redirectAttributes.addFlashAttribute("createCompleteMSG", name + "を作成しました。");

        return "redirect:/manager/employeeList";
    }



//--テスト範囲-------------------------------------------
    // ユーザー一括登録画面遷移処理
    @GetMapping("/allcreate")
    public String NewUserAllRegistrationInformation(Model model){
        model.addAttribute("csvNull",false);
        return "/manager/all-create";
    }

    //ひな形ダウンロード画面遷移
    @GetMapping("/allCreateDownload")
    public String allCreate(){
        return "/manager/all-createDownload";
    }

    ///manager/all-createで登録ボタンを押下時の処理
    @PostMapping("/csvUpload")
    public String uploadCsv(@RequestParam("file") MultipartFile file, Model model) {
        try {
            //ファイルが選択されていない時の処理
            if (file.isEmpty()){
                model.addAttribute("csvNull",true);
                return "/manager/all-create";
            }
            // CSVファイルの内容を行ごとに読み取る
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));//Shift_JIS_MS932
            List<String[]> csvData = new ArrayList<>();
            List<String[]> conversionCsvData = new ArrayList<>();
            //boolean csvCheck = false;
            boolean register = false;
            boolean judge = false;
            boolean msg = false;
            String line;

            while ((line = reader.readLine()) != null) {
                // 各行をカンマで分割して文字列配列に変換
                String[] row = line.split(",");
                csvData.add(row);
            }

            // ここでcsvDataを使って必要な処理を行う
            //foreachでリストを回す
            List<String[]> newCsvData = new ArrayList<>();
            List<String[]> duplicationCsvData = new ArrayList<>();
            String icon = "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";
            csvData.remove(0);
            for (int i = 0; i < csvData.size(); i++) {
                String[] row = csvData.get(i);
                boolean duplicateEmployee = false;
                boolean duplicateEmail = false;
                String duplicateComment = "";
                //各要素を回す
                for (int j = 0; j < row.length; j++) {
                    String employee = row[j];
                    switch (j) {
                        case 0://社員コード(employee_code)の重複を確認
                            duplicateEmployee = userService.DuplicateEmployeeCodeConfirmation(Integer.parseInt(employee));
                            break;
                        case 3://メールアドレス(email)の重複を確認
                            duplicateEmail = userService.DuplicateEmailConfirmation(employee);
                    }
                    if (duplicateEmployee && duplicateEmail) {
                        duplicateComment = "該当の社員コードの情報を更新します。";
                        msg = true;
                    } else if (duplicateEmployee) {
                        duplicateComment = "該当の社員コードの情報を更新します。";
                        msg = true;
                    } else if (duplicateEmail) {
                        duplicateComment = "メールアドレスが重複";
                        register = true;
                        judge = true;
                    } else {
                        duplicateComment = "OK";
                    }
                }
                if (judge){
                    String[] newRow = new String[row.length + 1];
                    System.arraycopy(row, 0, newRow, 0, row.length);
                    newRow[row.length] = duplicateComment;
                    duplicationCsvData.add(newRow);
                }else{
                    String[] newRow = new String[row.length + 1];
                    System.arraycopy(row, 0, newRow, 0, row.length);
                    newRow[row.length] = duplicateComment;
                    newCsvData.add(newRow);
                }
            }
            if (register){
                csvData = duplicationCsvData;
            }else {
                csvData=newCsvData;
            }
            //csvData.remove(0);
            model.addAttribute("csvData",csvData);
            //model.addAttribute("duplicationCsvData",duplicationCsvData);
            //model.addAttribute("csvCheck",csvCheck);
            model.addAttribute("userCreateInput", new UserCreateInput());
            model.addAttribute("icon",icon);
            model.addAttribute("register",register);
            model.addAttribute("msg",msg);
//            model.addAttribute("userAllCreateInput", new UserAllCreateInput());

            // 遷移先のビューを返す
            return "/manager/allCreateCheck";
        } catch (IOException e) {
            // エラーハンドリング
            e.printStackTrace();
            return "redirect:/manager/error-page";
        }
    }


    @PostMapping("/allAddEmployee")
    public String allAddNewUserRegistration(@ModelAttribute("userCreateInput") UserCreateInput userCreateInput) {
        //アイコンを取得する
        for (int i = 0; i < userCreateInput.getUserAllCreateInputs().size(); i++) {
            UserAllCreateInput user = userCreateInput.getUserAllCreateInputs().get(i);
            user.setConvertIcon("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");
        }
        AssignmentCreateInput assignmentCreateInput = new AssignmentCreateInput();
        for (int i = 0; i < userCreateInput.getUserAllCreateInputs().size(); i++) {
            //employeeCodeを使用し、DBにデータがあるか取得する
            boolean employeeCheck = userService.DuplicateEmployeeCodeConfirmation(userCreateInput.getUserAllCreateInputs().get(i).getEmployeeCode());
            User user = userService.selectUserById(userCreateInput.getUserAllCreateInputs().get(i).getEmployeeCode());
            //共通
            userCreateInput.setEmployeeCode(userCreateInput.getUserAllCreateInputs().get(i).getEmployeeCode());
            userCreateInput.setName(userCreateInput.getUserAllCreateInputs().get(i).getName());
            userCreateInput.setEmail(userCreateInput.getUserAllCreateInputs().get(i).getEmail());
            userCreateInput.setRole(userCreateInput.getUserAllCreateInputs().get(i).getRole());
            userCreateInput.setConvertIcon(userCreateInput.getUserAllCreateInputs().get(i).getConvertIcon());
            if (employeeCheck){
                //登録済み：パスワードとアイコンは既存のデータを引用
                userCreateInput.setPassword(user.getPassword());
                userService.encodePassword(userCreateInput);
                userCreateInput.setConvertIcon(user.getIcon());
            }else {
                //未登録：パスワードとアイコンは新規で登録
                userCreateInput.setPassword(userCreateInput.getUserAllCreateInputs().get(i).getPassword());
                userService.encodePassword(userCreateInput);
                userCreateInput.setConvertIcon(userCreateInput.getUserAllCreateInputs().get(i).getConvertIcon());
            }

            //assignmentCreateInputにteamIdとisManagerとemployeeCodeをセットする
            //teamIdの設定・取得方法
            //DBに同じチーム名があるか取得する
            //Mapperの新規作成、nameと一致したIdを取得する
            String teamName = userCreateInput.getUserAllCreateInputs().get(i).getTeamName();
            Integer teamId = teamService.selectTeamIdByName(teamName);
            //有無によって条件分岐
            if (teamId != null){
                //ある場合（Not Null）
                //teamIdをsetする
                assignmentCreateInput.setTeamId(teamId);
            } else {
                //無い場合(Null）
                //teaNameでteamIdが取得できなかった場合、既存データに無いということなので新規作成する（csvで半角、全角で入力違いがあったら、、、については未対応）
                //Mapperのvoid insertTeam(Team team)を使用し、新規作成する。
                //Team内には、nameとreleaseがある。
                teamId = teamService.create(
                    userCreateInput.getUserAllCreateInputs().get(i).getTeamName(),
                    false
                );
            }
            assignmentCreateInput.setTeamId(teamId);
            //isManagerの設定・取得方法
            //userCreateInput.getUserAllCreateInputs().get(i).getTeamRole()から
            //managerならtrueをmemberならfalseをセットするように条件分岐する
            if(userCreateInput.getUserAllCreateInputs().get(i).getTeamRole().equals("member")) {
                assignmentCreateInput.setIsManager(false);
            } else if (userCreateInput.getUserAllCreateInputs().get(i).getTeamRole().equals("manager")) {
                assignmentCreateInput.setIsManager(true);
            }
            //employeeCodeの設定・取得方法
            //userCreateInput.getUserAllCreateInputs().get(i).getEmployeeCode())から取得する
            assignmentCreateInput.setEmployeeCode(userCreateInput.getUserAllCreateInputs().get(i).getEmployeeCode());

            //DB登録
            //登録の有無で変更
            if (employeeCheck){
                userService.updateEmployee(userCreateInput);
                assignmentService.update(
                        assignmentCreateInput.getEmployeeCode(),
                        assignmentCreateInput.getIsManager(),
                        assignmentCreateInput.getTeamId()
                );
            }else{
                userService.createEmployeeInformation(userCreateInput);
                int newAssignmentId = assignmentService.create(
                        assignmentCreateInput.getEmployeeCode(),
                        assignmentCreateInput.getIsManager(),
                        assignmentCreateInput.getTeamId()
                );
            }


        }

        return "redirect:/manager/employeeList";
    }



//--テスト範囲-------------------------------------------

    @GetMapping("/employeeList")
    public String displayEmployeeList(Model model, @ModelAttribute UserSearchInput userSearchInput, RedirectAttributes redirectAttributes) {

        String title = "ユーザー一覧";
        model.addAttribute("title", title);

        List<User> userList = new ArrayList<User>();

        if (userSearchInput.getSearchKeyword() != null ){
            if (userSearchInput.getSearchWords() == null || Objects.equals(userSearchInput.getSearchWords(), ""))
            {
                userList = userService.getAllEmployeeInfo();
                model.addAttribute("userList", userList);
                return "manager/employeeList";
            }
            try {
            switch (userSearchInput.getSearchKeyword()){
                case "社員番号":
                    if (isNumeric(userSearchInput.getSearchWords())){
                    int num = Integer.parseInt(userSearchInput.getSearchWords());
                    if (userService.getUserByCode(num) != null) {
                        userList = userService.getUserByCode(num);
                        break;
                    }
                    }
                case "名前":
                    if(userService.getUserByName(userSearchInput.getSearchWords()) != null){
                        userList = userService.getUserByName(userSearchInput.getSearchWords());
                        break;
                    }
                case "役割":
                    if (userService.getUserByRole(userSearchInput.getSearchWords()) != null){
                        userList = userService.getUserByRole(userSearchInput.getSearchWords());
                        break;
                    }
            }
            } catch (Exception e){
                return "redirect:manager/employeeList";
            }
                model.addAttribute("userList", userList);
                return "manager/employeeList";
        }
            userList = userService.getAllEmployeeInfo();

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
    public String displayTeamList(Model model, RedirectAttributes redirectAttributes){
        List<Team> teamList = teamService.getAllTeam();
        List<User> allUser = userService.getAllEmployeeInfo();
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
                teamCreateInput.getName(),
                teamCreateInput.getRelase()
        );

        Team team = teamService.getTeamById(newTeamId);
        String name = team.getName();

        redirectAttributes.addFlashAttribute("createCompleteMSG", name + "を作成しました。");

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
        String name = team.getName();

        redirectAttributes.addAttribute("teamId", teamUpdateInput.getTeamId());
        redirectAttributes.addFlashAttribute("editCompleteMSG", name + "を編集しました。");

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
        List<User> allUser = userService.getAllEmployeeInfo();
        List<User>users=new ArrayList<>();
        for(User user : allUser){
            if(!user.getName().equals("SuperAdmin")){
                users.add(userService.getUserByEmployeeCode(user.getEmployeeCode()));
            }
        }

        model.addAttribute("team", team);
        model.addAttribute("users", users);
//        model.addAttribute("assignmentCreateInput", new AssignmentCreateInput());

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
        model.addAttribute("managers", managers);
        model.addAttribute("members", members);
        model.addAttribute("assignmentAllCreateInput",new AssignmentAllCreateInput());
        return "manager/assignment-create";
    }

    @Transactional
    @PostMapping("/assignment/create")
    public String creatingAssignment(AssignmentCreateInput assignmentCreateInput, RedirectAttributes redirectAttributes,@ModelAttribute("assignmentAllCreateInput")AssignmentAllCreateInput assignmentAllCreateInput){

        //assignmentAllCreateInputで取得したmanagerとmemberのemployeeCodeを,区切りで分割する
        String[] managerArray = assignmentAllCreateInput.getManagerList().split(",");
        String[] memberArray = assignmentAllCreateInput.getMemberList().split(",");
        //,区切りで分割した各要素にある空白を削除
        for (int i = 0; i < managerArray.length; i++) {
            managerArray[i] = managerArray[i].trim();
        }
        for (int i = 0; i < memberArray.length; i++) {
            memberArray[i] = memberArray[i].trim();
        }

        if (assignmentCreateInput.getTeamId() != 0 && !assignmentService.existsAssignment(assignmentCreateInput.getEmployeeCode(), assignmentCreateInput.getTeamId())) {
            //TeamIdに紐づく全てを削除する
            assignmentService.deleteByTeam(assignmentCreateInput.getTeamId());
            //Managerループ
            for (String employeeCode : managerArray) {
                int newAssignment = assignmentService.create(Integer.parseInt(employeeCode), true, assignmentCreateInput.getTeamId());
            }
            //Memberループ
            for (String employeeCode : memberArray) {
                int newAssignment = assignmentService.create(Integer.parseInt(employeeCode),false, assignmentCreateInput.getTeamId());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorAstMsg", "該当のユーザーはすでに追加されています");
            int teamId = assignmentCreateInput.getTeamId();
            redirectAttributes.addAttribute("teamId", teamId);
            return "redirect:/manager/teams/{teamId}/detail";
        }

        int teamId = assignmentCreateInput.getTeamId();
        redirectAttributes.addAttribute("teamId", teamId);
        redirectAttributes.addFlashAttribute("createCompleteMSG", "チームのメンバー編集を行いました。");

        return "redirect:/manager/teams/{teamId}/detail";
    }

    @Transactional
    @PostMapping("/assignment/{teamId}/{employeeCode}/delete")
    public String deleteAst(@PathVariable int teamId, @PathVariable int employeeCode, RedirectAttributes redirectAttributes){
        List<Assignment> assignments = assignmentService.getAssignmentByTeam(teamId);
        Assignment ast = new Assignment();

        for (Assignment as : assignments){
            if (as.getEmployeeCode() == employeeCode){
                ast = as;
            }
        }

        assignmentService.deleteById(ast.getAssignmentId());
        redirectAttributes.addFlashAttribute("deleteCompleteMSG", "該当のメンバーをチームから削除しました。");

        return "redirect:/manager/teams/{teamId}/detail";
    }


    @PostMapping("/teams/{teamId}/delete")
    @Transactional
    public String deleteTeam(
            @PathVariable int teamId, RedirectAttributes redirectAttributes
    ) {
        Team team = teamService.getTeamById(teamId);
        String name = team.getName();

        this.assignmentService.deleteByTeam(teamId);
        this.teamService.deleteById(teamId);
        redirectAttributes.addFlashAttribute("deleteCompleteMSG", name + "を削除しました。");

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

    //規定の業務時間
    @GetMapping("/setting-time")
    public String settingTime(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Setting setting = settingService.getSettingTime(employeeCode);

        model.addAttribute("setting",setting);
        model.addAttribute("SettingInput",new SettingInput());

        return "manager/setting";
    }
    @PostMapping("/setting-time/edit")
    public String settingTimeEdit(@ModelAttribute("SettingInput") SettingInput settingInput,
                                  Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        settingService.update(settingInput, employeeCode);
        Setting setting = settingService.getSettingTime(employeeCode);
        model.addAttribute("setting",setting);
        model.addAttribute("SettingInput",settingInput);
//        redirectAttributes.addFlashAttribute("addCompleteMSG", "始業時間を『" + setting.getStartTime() + "』、終業時間を『" + setting.getEndTime() + "』に設定しました。");
        return "manager/setting";
    }

    private static boolean isNumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
