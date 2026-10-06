package com.placepro.controller;

import com.placepro.dto.CompanyDTO;
import com.placepro.dto.StudentDTO;
import com.placepro.model.Application;
import com.placepro.model.Company;
import com.placepro.model.PlacementDrive;
import com.placepro.model.User;
import com.placepro.repository.ApplicationRepository;
import com.placepro.repository.CompanyRepository;
import com.placepro.repository.PlacementDriveRepository;
import com.placepro.repository.UserRepository;
import com.placepro.service.AIRecommendationService;
import com.placepro.service.CompanyService;
import com.placepro.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final CompanyService companyService;
    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final AIRecommendationService aiRecommendationService;

    @Autowired
    public AdminController(StudentService studentService,
                           CompanyService companyService,
                           PlacementDriveRepository driveRepository,
                           ApplicationRepository applicationRepository,
                           UserRepository userRepository,
                           AIRecommendationService aiRecommendationService) {
        this.studentService = studentService;
        this.companyService = companyService;
        this.driveRepository = driveRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.aiRecommendationService = aiRecommendationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        String username = principal != null ? principal.getName() : "admin1";
        User currentAdmin = userRepository.findByUsername(username).orElse(null);
        Long adminId = currentAdmin != null ? currentAdmin.getId() : null;

        List<StudentDTO> myStudents = (adminId != null)
                ? studentService.getStudentsByAssignedAdmin(adminId)
                : studentService.getAllStudents();

        List<CompanyDTO> companies = companyService.getAllCompanies();
        List<PlacementDrive> drives = driveRepository.findAll();
        List<Application> allApplications = applicationRepository.findAll();

        List<Application> myApplications = (adminId != null)
                ? allApplications.stream().filter(a -> a.getStudent().getAssignedAdmin() != null && a.getStudent().getAssignedAdmin().getId().equals(adminId)).toList()
                : allApplications;

        long placedCount = myApplications.stream()
                .filter(a -> "PLACED".equalsIgnoreCase(a.getStatus()) || "SELECTED".equalsIgnoreCase(a.getStatus()))
                .map(a -> a.getStudent().getId())
                .distinct()
                .count();

        double placementPct = !myStudents.isEmpty()
                ? Math.round((double) placedCount / myStudents.size() * 100.0 * 10.0) / 10.0
                : 0.0;

        // Status Breakdown for Logged-In Officer
        Map<String, Long> statusMap = new LinkedHashMap<>();
        statusMap.put("PLACED", 0L);
        statusMap.put("SHORTLISTED", 0L);
        statusMap.put("INTERVIEW", 0L);
        statusMap.put("APPLIED", 0L);
        statusMap.put("REJECTED", 0L);

        for (Application a : myApplications) {
            String st = a.getStatus() != null ? a.getStatus().toUpperCase() : "APPLIED";
            if ("SELECTED".equals(st)) st = "PLACED";
            statusMap.put(st, statusMap.getOrDefault(st, 0L) + 1);
        }

        model.addAttribute("currentAdmin", currentAdmin);
        model.addAttribute("myStudentsCount", myStudents.size());
        model.addAttribute("totalCompanies", companies.size());
        model.addAttribute("activeDrives", drives.stream().filter(d -> "OPEN".equalsIgnoreCase(d.getStatus())).count());
        model.addAttribute("totalApplications", myApplications.size());
        model.addAttribute("selectedStudents", placedCount);
        model.addAttribute("placementPercentage", placementPct);
        model.addAttribute("statusWiseApplications", statusMap);

        return "admin-dashboard";
    }

    // DEDICATED DEPARTMENT-WISE PLACEMENT RECORDS PAGE
    @GetMapping("/department-records")
    public String departmentPlacementRecords(Principal principal, Model model) {
        String currentUsername = principal != null ? principal.getName() : "admin1";
        User currentAdmin = userRepository.findByUsername(currentUsername).orElse(null);

        List<StudentDTO> allStudents = studentService.getAllStudents();
        List<Application> placedApps = applicationRepository.findAll().stream()
                .filter(a -> "PLACED".equalsIgnoreCase(a.getStatus()) || "SELECTED".equalsIgnoreCase(a.getStatus()))
                .toList();

        Map<String, Long> deptTotalMap = new LinkedHashMap<>();
        Map<String, Long> deptPlacedMap = new LinkedHashMap<>();
        String[] depts = {"CSE", "IT", "AIDS", "ECE", "EEE", "MECH", "CIVIL"};
        for (String d : depts) {
            deptTotalMap.put(d, 0L);
            deptPlacedMap.put(d, 0L);
        }

        for (StudentDTO s : allStudents) {
            String deptName = s.getDepartment();
            if (deptName != null) {
                String code = deptName.trim().toUpperCase();
                if (code.equals("CSE") || code.contains("COMPUTER")) deptTotalMap.put("CSE", deptTotalMap.get("CSE") + 1);
                else if (code.equals("IT") || code.contains("INFORMATION")) deptTotalMap.put("IT", deptTotalMap.get("IT") + 1);
                else if (code.equals("AIDS") || code.contains("AI") || code.contains("DATA")) deptTotalMap.put("AIDS", deptTotalMap.get("AIDS") + 1);
                else if (code.equals("ECE") || code.contains("ELECTRONICS")) deptTotalMap.put("ECE", deptTotalMap.get("ECE") + 1);
                else if (code.equals("EEE") || code.contains("ELECTRICAL")) deptTotalMap.put("EEE", deptTotalMap.get("EEE") + 1);
                else if (code.equals("MECH") || code.contains("MECHANICAL")) deptTotalMap.put("MECH", deptTotalMap.get("MECH") + 1);
                else if (code.equals("CIVIL")) deptTotalMap.put("CIVIL", deptTotalMap.get("CIVIL") + 1);
            }
        }

        for (Application app : placedApps) {
            String deptName = app.getStudent().getDepartment();
            if (deptName != null) {
                String code = deptName.trim().toUpperCase();
                if (code.equals("CSE") || code.contains("COMPUTER")) deptPlacedMap.put("CSE", deptPlacedMap.get("CSE") + 1);
                else if (code.equals("IT") || code.contains("INFORMATION")) deptPlacedMap.put("IT", deptPlacedMap.get("IT") + 1);
                else if (code.equals("AIDS") || code.contains("AI") || code.contains("DATA")) deptPlacedMap.put("AIDS", deptPlacedMap.get("AIDS") + 1);
                else if (code.equals("ECE") || code.contains("ELECTRONICS")) deptPlacedMap.put("ECE", deptPlacedMap.get("ECE") + 1);
                else if (code.equals("EEE") || code.contains("ELECTRICAL")) deptPlacedMap.put("EEE", deptPlacedMap.get("EEE") + 1);
                else if (code.equals("MECH") || code.contains("MECHANICAL")) deptPlacedMap.put("MECH", deptPlacedMap.get("MECH") + 1);
                else if (code.equals("CIVIL")) deptPlacedMap.put("CIVIL", deptPlacedMap.get("CIVIL") + 1);
            }
        }

        model.addAttribute("totalStudents", allStudents.size());
        model.addAttribute("totalPlaced", placedApps.size());
        model.addAttribute("deptTotalMap", deptTotalMap);
        model.addAttribute("deptPlacedMap", deptPlacedMap);
        model.addAttribute("currentAdmin", currentAdmin);
        return "department-records";
    }

    // STUDENT MANAGEMENT (DISPLAYS LOGGED-IN OFFICER'S DISTINCT STUDENTS BY DEFAULT)
    @GetMapping("/students")
    public String listStudents(@RequestParam(required = false) String query,
                               @RequestParam(required = false) String dept,
                               @RequestParam(required = false) Double minCgpa,
                               Principal principal,
                               Model model) {
        String currentUsername = principal != null ? principal.getName() : "admin1";
        User currentAdmin = userRepository.findByUsername(currentUsername).orElse(null);
        Long currentAdminId = currentAdmin != null ? currentAdmin.getId() : null;

        List<StudentDTO> students = studentService.searchStudents(query, dept, minCgpa, currentAdminId);

        model.addAttribute("students", students);
        model.addAttribute("query", query);
        model.addAttribute("dept", dept);
        model.addAttribute("minCgpa", minCgpa);
        model.addAttribute("currentAdmin", currentAdmin);
        return "students";
    }

    @GetMapping("/students/new")
    public String newStudentForm(Principal principal, Model model) {
        String currentUsername = principal != null ? principal.getName() : "admin1";
        User currentAdmin = userRepository.findByUsername(currentUsername).orElse(null);
        
        StudentDTO dto = new StudentDTO();
        if (currentAdmin != null) {
            dto.setAssignedAdminId(currentAdmin.getId());
        }
        model.addAttribute("studentDto", dto);
        model.addAttribute("isEdit", false);
        return "student-form";
    }

    @PostMapping("/students")
    public String saveStudent(@Valid @ModelAttribute("studentDto") StudentDTO studentDto,
                               BindingResult bindingResult,
                               Principal principal,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "student-form";
        }
        try {
            String creatorUsername = principal != null ? principal.getName() : "admin1";
            studentService.createStudent(studentDto, creatorUsername);
            redirectAttributes.addFlashAttribute("successMessage", "Student profile created successfully under your officer records!");
            return "redirect:/admin/students";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "student-form";
        }
    }

    @GetMapping("/students/{id}")
    public String viewStudentDetails(@PathVariable Long id, Model model) {
        StudentDTO student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "student-details";
    }

    @GetMapping("/students/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        StudentDTO student = studentService.getStudentById(id);
        model.addAttribute("studentDto", student);
        model.addAttribute("isEdit", true);
        return "student-form";
    }

    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute("studentDto") StudentDTO studentDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "student-form";
        }
        studentService.updateStudent(id, studentDto);
        redirectAttributes.addFlashAttribute("successMessage", "Student profile updated successfully!");
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student profile deleted successfully!");
        return "redirect:/admin/students";
    }

    // COMPANY MANAGEMENT
    @GetMapping("/companies")
    public String listCompanies(@RequestParam(required = false) String query, Model model) {
        List<CompanyDTO> companies = companyService.searchCompanies(query);
        model.addAttribute("companies", companies);
        model.addAttribute("query", query);
        return "companies";
    }

    @GetMapping("/companies/new")
    public String newCompanyForm(Model model) {
        model.addAttribute("companyDto", new CompanyDTO());
        model.addAttribute("isEdit", false);
        return "company-form";
    }

    @PostMapping("/companies")
    public String saveCompany(@Valid @ModelAttribute("companyDto") CompanyDTO companyDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "company-form";
        }
        companyService.createCompany(companyDto);
        redirectAttributes.addFlashAttribute("successMessage", "Company record added successfully!");
        return "redirect:/admin/companies";
    }

    @GetMapping("/companies/{id}")
    public String viewCompanyDetails(@PathVariable Long id, Model model) {
        CompanyDTO company = companyService.getCompanyById(id);
        model.addAttribute("company", company);
        return "company-details";
    }

    @GetMapping("/companies/{id}/edit")
    public String editCompanyForm(@PathVariable Long id, Model model) {
        CompanyDTO company = companyService.getCompanyById(id);
        model.addAttribute("companyDto", company);
        model.addAttribute("isEdit", true);
        return "company-form";
    }

    @PostMapping("/companies/{id}")
    public String updateCompany(@PathVariable Long id,
                                @Valid @ModelAttribute("companyDto") CompanyDTO companyDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "company-form";
        }
        companyService.updateCompany(id, companyDto);
        redirectAttributes.addFlashAttribute("successMessage", "Company record updated successfully!");
        return "redirect:/admin/companies";
    }

    @PostMapping("/companies/{id}/delete")
    public String deleteCompany(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        companyService.deleteCompany(id);
        redirectAttributes.addFlashAttribute("successMessage", "Company record deleted!");
        return "redirect:/admin/companies";
    }

    // PLACEMENT DRIVES
    @GetMapping("/placement-drives")
    public String listDrives(Model model) {
        List<PlacementDrive> drives = driveRepository.findAll();
        model.addAttribute("drives", drives);
        return "placement-drives";
    }

    @GetMapping("/placement-drives/new")
    public String newDriveForm(Model model) {
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("drive", new PlacementDrive());
        model.addAttribute("isEdit", false);
        return "placement-drive-form";
    }

    @PostMapping("/placement-drives")
    public String saveDrive(@RequestParam Long companyId,
                            @RequestParam String jobRole,
                            @RequestParam String driveDate,
                            @RequestParam String applicationDeadline,
                            @RequestParam(required = false) String eligibilityDescription,
                            RedirectAttributes redirectAttributes) {
        CompanyDTO companyDto = companyService.getCompanyById(companyId);
        PlacementDrive drive = new PlacementDrive();
        drive.setCompany(companyService.convertToEntity(companyDto));
        drive.setJobRole(jobRole);
        drive.setDriveDate(LocalDate.parse(driveDate));
        drive.setApplicationDeadline(LocalDate.parse(applicationDeadline));
        drive.setStatus("OPEN");
        drive.setEligibilityDescription(eligibilityDescription);

        driveRepository.save(drive);
        redirectAttributes.addFlashAttribute("successMessage", "Placement drive created!");
        return "redirect:/admin/placement-drives";
    }

    @GetMapping("/placement-drives/{id}/edit")
    public String editDriveForm(@PathVariable Long id, Model model) {
        PlacementDrive drive = driveRepository.findById(id).orElseThrow();
        model.addAttribute("drive", drive);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("isEdit", true);
        return "placement-drive-form";
    }

    @PostMapping("/placement-drives/{id}")
    public String updateDrive(@PathVariable Long id,
                              @RequestParam Long companyId,
                              @RequestParam String jobRole,
                              @RequestParam String driveDate,
                              @RequestParam String applicationDeadline,
                              @RequestParam String status,
                              @RequestParam(required = false) String eligibilityDescription,
                              RedirectAttributes redirectAttributes) {
        PlacementDrive drive = driveRepository.findById(id).orElseThrow();
        CompanyDTO companyDto = companyService.getCompanyById(companyId);

        drive.setCompany(companyService.convertToEntity(companyDto));
        drive.setJobRole(jobRole);
        drive.setDriveDate(LocalDate.parse(driveDate));
        drive.setApplicationDeadline(LocalDate.parse(applicationDeadline));
        drive.setStatus(status);
        drive.setEligibilityDescription(eligibilityDescription);

        driveRepository.save(drive);
        redirectAttributes.addFlashAttribute("successMessage", "Placement drive updated!");
        return "redirect:/admin/placement-drives";
    }

    @PostMapping("/placement-drives/{id}/close")
    public String closeDrive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        PlacementDrive drive = driveRepository.findById(id).orElseThrow();
        drive.setStatus("CLOSED");
        driveRepository.save(drive);
        redirectAttributes.addFlashAttribute("successMessage", "Drive status set to CLOSED.");
        return "redirect:/admin/placement-drives";
    }

    // APPLICATIONS TRACKING (RESPONSIVE DEPARTMENT, STATUS & SEARCH QUERY FILTERING)
    @GetMapping("/applications")
    public String listApplications(@RequestParam(required = false) String query,
                                   @RequestParam(required = false) Long companyId,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String dept,
                                   Principal principal,
                                   Model model) {
        String currentUsername = principal != null ? principal.getName() : "admin1";
        User currentAdmin = userRepository.findByUsername(currentUsername).orElse(null);

        // If navigating from Department Records with dept param, set query to dept for search box pre-fill
        if ((query == null || query.trim().isEmpty()) && dept != null && !dept.trim().isEmpty()) {
            query = dept.trim();
        }

        List<Application> applications = applicationRepository.findAll();

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            applications = applications.stream().filter(a -> {
                String name = a.getStudent() != null && a.getStudent().getName() != null ? a.getStudent().getName().toLowerCase() : "";
                String reg = a.getStudent() != null && a.getStudent().getRegistrationNumber() != null ? a.getStudent().getRegistrationNumber().toLowerCase() : "";
                String department = a.getStudent() != null && a.getStudent().getDepartment() != null ? a.getStudent().getDepartment().toLowerCase() : "";
                String comp = a.getPlacementDrive() != null && a.getPlacementDrive().getCompany() != null && a.getPlacementDrive().getCompany().getName() != null ? a.getPlacementDrive().getCompany().getName().toLowerCase() : "";
                return name.contains(q) || reg.contains(q) || department.contains(q) || comp.contains(q);
            }).toList();
        }

        if (companyId != null) {
            applications = applications.stream().filter(a -> a.getPlacementDrive().getCompany().getId().equals(companyId)).toList();
        }
        if (status != null && !status.trim().isEmpty()) {
            String reqStatus = status.trim();
            if ("SELECTED".equalsIgnoreCase(reqStatus)) reqStatus = "PLACED";
            final String targetStatus = reqStatus;
            applications = applications.stream().filter(a -> a.getStatus().equalsIgnoreCase(targetStatus)).toList();
        }

        model.addAttribute("applications", applications);
        model.addAttribute("companies", companyService.getAllCompanies());
        model.addAttribute("query", query);
        model.addAttribute("selectedCompanyId", companyId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDept", dept);
        return "applications";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
                                           @RequestParam String status,
                                           RedirectAttributes redirectAttributes) {
        Application application = applicationRepository.findById(id).orElseThrow();
        application.setStatus(status);
        applicationRepository.save(application);
        redirectAttributes.addFlashAttribute("successMessage", "Application status updated to: " + status);
        return "redirect:/admin/applications";
    }

    // AI RECOMMENDATIONS PORTAL (SCOPED TO OFFICER'S STUDENTS)
    @GetMapping("/ai-recommendations")
    public String aiRecommendationsPortal(@RequestParam(required = false) Long studentId, Principal principal, Model model) {
        String currentUsername = principal != null ? principal.getName() : "admin1";
        User currentAdmin = userRepository.findByUsername(currentUsername).orElse(null);
        Long currentAdminId = currentAdmin != null ? currentAdmin.getId() : null;

        List<StudentDTO> students = (currentAdminId != null)
                ? studentService.getStudentsByAssignedAdmin(currentAdminId)
                : studentService.getAllStudents();
        model.addAttribute("students", students);

        if (studentId != null) {
            StudentDTO selectedStudent = studentService.getStudentById(studentId);
            model.addAttribute("selectedStudent", selectedStudent);

            var recommendations = aiRecommendationService.getCompanyRecommendations(selectedStudent.getId());
            var readiness = aiRecommendationService.assessPlacementReadiness(selectedStudent.getId());

            model.addAttribute("recommendations", recommendations);
            model.addAttribute("readiness", readiness);
        }

        return "ai-recommendations";
    }

    // ROUTE REPORTS & ANALYTICS TO DEPARTMENT PLACEMENT RECORDS
    @GetMapping("/reports")
    public String reports() {
        return "redirect:/admin/department-records";
    }
}
