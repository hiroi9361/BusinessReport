package analix.DHIT.controller;

import analix.DHIT.input.MemberSearchInput;
import analix.DHIT.input.ReportSearchInput;
import analix.DHIT.input.UserCreateInput;
import analix.DHIT.input.UserEditInput;
import analix.DHIT.model.Report;
import analix.DHIT.model.TaskLog;
import analix.DHIT.model.User;
import analix.DHIT.service.ReportService;
import analix.DHIT.service.TaskLogService;
import analix.DHIT.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final UserService userService;
    private final ReportService reportService;
    private final TaskLogService taskLogService;

    public ManagerController(
            UserService userservice,
            ReportService reportService,
            TaskLogService taskLogService
    ) {
        this.userService = userservice;
        this.reportService = reportService;
        this.taskLogService = taskLogService;
    }

    @GetMapping("/home")
    public String displayHome(
            Model model,
            @RequestParam(name = "searchCharacters", required = false) String searchCharacters
    ) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年M月d日(E)", Locale.JAPANESE));
        model.addAttribute("today", today);

        //アイコン探し
        if (searchCharacters == null) {
            model.addAttribute("members", userService.getAllMember());
            model.addAttribute("memberSearchInput", new MemberSearchInput());
            return "manager/home";
        }

        model.addAttribute("members", model.getAttribute("members"));
        model.addAttribute("memberSearchInput", new MemberSearchInput().withSearchCharacters(searchCharacters));
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
            Model model
    ) {
        User member = userService.getUserByEmployeeCode(employeeCode);

        model.addAttribute("member", member);
        model.addAttribute("reportSearchInput", new ReportSearchInput());
        model.addAttribute("error", model.getAttribute("error"));

        return "manager/report-search";
    }

    @PostMapping("/search-report")
    public String searchReport(
            ReportSearchInput reportSearchInput,
            RedirectAttributes redirectAttributes
    ) {
        String reportId = reportService.searchId(
                reportSearchInput.getEmployeeCode(),
                reportSearchInput.getDate()
        );

        if (reportId == null) {
            redirectAttributes.addFlashAttribute("error", "ヒットしませんでした");
            return "redirect:/manager/report-search?employeeCode=" + reportSearchInput.getEmployeeCode();
        }

        redirectAttributes.addAttribute("reportId", reportId);
        return "redirect:/manager/reports/{reportId}";
    }

    @GetMapping("/reports/{reportId}")
    public String displayReportDetail(@PathVariable("reportId") int reportId, Model model) {

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

        return "manager/report-detail";
    }

    @GetMapping("/create")
    public String display(Model model) {
        model.addAttribute("userCreateInput", new UserCreateInput());
        return "manager/create";
    }

    //↓新規社員情報入力処理
    @PostMapping("/createEmployee")
    public String NewUserRegistrationInformation(@ModelAttribute("UserCreateInput") UserCreateInput userCreateInput,
                                                 RedirectAttributes redirectAttributes){

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
        //作業完了画面に飛ばす
        return "manager/workCompletion";
    }
    @GetMapping("/employeeList")
    public String displayEmployeeList(Model model) {
        List<User> userList = userService.getAllEmployeeInfo();
        model.addAttribute("userList", userList);
        return "manager/employeeList";
    }
    //社員削除画面表示
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
        model.addAttribute("name", name);
        model.addAttribute("employeeCode", employeeCode);
        return "manager/employeeList-deleteUser";
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


}
