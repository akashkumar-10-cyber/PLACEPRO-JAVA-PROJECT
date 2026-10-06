package com.placepro.config;

import com.placepro.model.Application;
import com.placepro.model.Company;
import com.placepro.model.PlacementDrive;
import com.placepro.model.Student;
import com.placepro.model.User;
import com.placepro.repository.ApplicationRepository;
import com.placepro.repository.CompanyRepository;
import com.placepro.repository.PlacementDriveRepository;
import com.placepro.repository.StudentRepository;
import com.placepro.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           StudentRepository studentRepository,
                           CompanyRepository companyRepository,
                           PlacementDriveRepository driveRepository,
                           ApplicationRepository applicationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.driveRepository = driveRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Seeding demonstration data for PlacePro Placement Cell System...");

        if (userRepository.count() > 0) {
            log.info("Data already seeded. Skipping initialization.");
            return;
        }

        // 1. Seed Faculty Placement Cell Admins
        User admin1 = new User("admin1", passwordEncoder.encode("admin123"), "ROLE_ADMIN");
        User admin2 = new User("admin2", passwordEncoder.encode("admin2026"), "ROLE_ADMIN");
        User admin3 = new User("admin3", passwordEncoder.encode("admin3030"), "ROLE_ADMIN");

        userRepository.saveAll(List.of(admin1, admin2, admin3));

        // 2. Seed Non-Uniform Student Distributions Across Officers
        // Admin1: 82 students (CSE: 32, IT: 28, AIDS: 22)
        // Admin2: 58 students (ECE: 30, EEE: 22, AIDS: 6)
        // Admin3: 40 students (MECH: 24, CIVIL: 16)
        // Total = 180 students across college
        List<Student> admin1Students = new ArrayList<>();
        admin1Students.addAll(generateStudentsForOfficer("CSE", "CSE", 32, 1, admin1, "Java, Spring Boot, SQL, REST APIs, Git"));
        admin1Students.addAll(generateStudentsForOfficer("IT", "IT", 28, 33, admin1, "Python, Django, PostgreSQL, Docker, React"));
        admin1Students.addAll(generateStudentsForOfficer("AIDS", "AIDS", 22, 61, admin1, "Python, Machine Learning, TensorFlow, SQL, Data Science"));

        List<Student> admin2Students = new ArrayList<>();
        admin2Students.addAll(generateStudentsForOfficer("ECE", "ECE", 30, 1, admin2, "C++, Embedded Systems, IoT, MATLAB, Verilog"));
        admin2Students.addAll(generateStudentsForOfficer("EEE", "EEE", 22, 31, admin2, "C, MATLAB, Simulink, Embedded C, Power Systems"));
        admin2Students.addAll(generateStudentsForOfficer("AIDS", "AIDS", 6, 83, admin2, "Python, AI Models, PyTorch, R, Deep Learning"));

        List<Student> admin3Students = new ArrayList<>();
        admin3Students.addAll(generateStudentsForOfficer("MECH", "MECH", 24, 1, admin3, "AutoCAD, SolidWorks, ANSYS, Python, Manufacturing"));
        admin3Students.addAll(generateStudentsForOfficer("CIVIL", "CIVIL", 16, 25, admin3, "AutoCAD, STAAD Pro, Revit, Civil 3D, Structural Analysis"));

        List<Student> allStudents = new ArrayList<>();
        allStudents.addAll(admin1Students);
        allStudents.addAll(admin2Students);
        allStudents.addAll(admin3Students);

        // Shuffle student records so management lists look intermingled
        Collections.shuffle(allStudents, new Random(77777));
        studentRepository.saveAll(allStudents);

        // 3. Seed Companies (21 total)
        List<Company> companies = Arrays.asList(
            // Software & AI (10)
            new Company("Zoho Corporation", "Software & Cloud Services", "Chennai", "7.0 - 12.5 LPA", 7.0, 0, "Java, Spring Boot, SQL, JavaScript, Git", "Eligible: CSE, IT, AIDS"),
            new Company("TCS Digital", "IT & Consulting", "Bengaluru", "7.0 - 9.0 LPA", 6.5, 1, "Java, Python, SQL, Data Structures", "Eligible: CSE, IT, AIDS"),
            new Company("Infosys Specialist", "Technology Services", "Hyderabad", "8.0 - 14.0 LPA", 7.5, 0, "Java, Microservices, React, Cloud, SQL", "Eligible: CSE, IT, AIDS"),
            new Company("Google Cloud India", "Cloud Computing & AI", "Bengaluru", "18.0 - 24.0 LPA", 8.5, 0, "Java, Go, Python, Cloud Architectures, Algorithms", "Eligible: CSE, IT, AIDS"),
            new Company("Microsoft Azure", "Software & Cloud Systems", "Hyderabad", "16.0 - 22.0 LPA", 8.2, 0, "C#, C++, Data Structures, System Design", "Eligible: CSE, IT, AIDS"),
            new Company("Amazon AWS India", "Cloud & Backend Infrastructure", "Chennai", "15.0 - 20.0 LPA", 8.0, 0, "Java, AWS, Distributed Systems, SQL", "Eligible: CSE, IT, AIDS"),
            new Company("Wipro Turbo", "Enterprise Solutions", "Kochi", "6.5 - 8.5 LPA", 6.8, 1, "Java, Python, SQL, HTML/CSS", "Eligible: CSE, IT, AIDS"),
            new Company("Accenture Digital", "Digital Engineering", "Pune", "7.2 - 10.0 LPA", 7.0, 0, "Java, React, Node.js, SQL", "Eligible: CSE, IT, AIDS"),
            new Company("Mindtree Technologies", "Software Systems", "Bengaluru", "6.8 - 9.2 LPA", 6.8, 1, "C++, Java, SQL, Cloud", "Eligible: CSE, IT, AIDS"),
            new Company("Tech Mahindra Cloud", "Cloud & DevOps Solutions", "Noida", "6.5 - 8.8 LPA", 6.5, 1, "Linux, Python, Docker, Cloud", "Eligible: CSE, IT, AIDS"),

            // Electronics & Power (6)
            new Company("Bosch India", "Automotive & Embedded Tech", "Coimbatore", "7.5 - 10.5 LPA", 7.5, 0, "C++, Embedded Systems, IoT, MATLAB, Python", "Eligible: ECE, EEE"),
            new Company("Qualcomm India", "Semiconductor & Mobile Tech", "Bengaluru", "14.0 - 18.0 LPA", 8.0, 0, "Verilog, C++, Embedded C, VLSI, Signal Processing", "Eligible: ECE, EEE"),
            new Company("Texas Instruments", "Analog & Microcontrollers", "Bengaluru", "13.0 - 17.0 LPA", 8.0, 0, "C, Embedded C, Circuit Design, MATLAB", "Eligible: ECE, EEE"),
            new Company("Intel India", "Processor Architecture", "Hyderabad", "15.0 - 19.0 LPA", 8.2, 0, "SystemVerilog, C++, FPGA, Computer Architecture", "Eligible: ECE, EEE"),
            new Company("Samsung Semiconductor", "Memory & Mobile Displays", "Noida", "10.0 - 14.0 LPA", 7.5, 0, "C++, Embedded Systems, Microcontrollers", "Eligible: ECE, EEE"),
            new Company("Siemens Energy & Power", "Industrial Automation", "Gurugram", "7.0 - 9.5 LPA", 7.0, 1, "PLC, SCADA, MATLAB, Power Systems", "Eligible: ECE, EEE"),

            // Core & Civil (5)
            new Company("L&T Technology Services", "Core Engineering R&D", "Mumbai", "6.5 - 8.5 LPA", 6.8, 1, "AutoCAD, SolidWorks, C++, Data Analytics", "Eligible: MECH, CIVIL"),
            new Company("Tata Motors R&D", "Automotive Engineering", "Pune", "8.0 - 11.0 LPA", 7.2, 0, "CATIA, SolidWorks, Finite Element Analysis, MATLAB", "Eligible: MECH, CIVIL"),
            new Company("Mahindra & Mahindra", "Automotive & Manufacturing", "Chennai", "7.5 - 10.0 LPA", 7.0, 0, "ANSYS, SolidWorks, Manufacturing Processes", "Eligible: MECH, CIVIL"),
            new Company("Larsen & Toubro Construction", "Civil & Structural Infrastructure", "Chennai", "6.8 - 9.0 LPA", 6.8, 1, "AutoCAD, STAAD Pro, Structural Engineering", "Eligible: MECH, CIVIL"),
            new Company("Ashok Leyland", "Commercial Vehicles & Heavy Machinery", "Hosur", "7.0 - 9.2 LPA", 7.0, 0, "AutoCAD, Thermal Engineering, Fluid Dynamics", "Eligible: MECH, CIVIL")
        );

        companyRepository.saveAll(companies);

        // 4. Seed Drives (14 OPEN, 7 CLOSED)
        List<PlacementDrive> drives = new ArrayList<>();
        int driveIdx = 0;
        for (Company comp : companies) {
            String role = "Graduate Trainee / SDE";
            if (comp.getName().contains("Zoho") || comp.getName().contains("TCS") || comp.getName().contains("Google")) {
                role = "Software Development Engineer (SDE-1)";
            } else if (comp.getName().contains("Bosch") || comp.getName().contains("Qualcomm")) {
                role = "Embedded Hardware Engineer";
            } else if (comp.getName().contains("Tata") || comp.getName().contains("L&T Construction")) {
                role = "Graduate Engineer Trainee (GET)";
            }

            String driveStatus = (driveIdx % 3 == 2) ? "CLOSED" : "OPEN";

            PlacementDrive drive = new PlacementDrive(
                comp,
                role,
                LocalDate.now().plusDays(10 + drives.size()),
                LocalDate.now().plusDays(5 + drives.size()),
                driveStatus,
                comp.getDescription()
            );
            drives.add(drive);
            driveIdx++;
        }
        driveRepository.saveAll(drives);

        // 5. Seed Distinct Application Status Distributions per Officer
        // Admin1 (82 students): 75 PLACED, 4 SHORTLISTED, 2 INTERVIEW, 1 APPLIED (91.5% placement)
        // Admin2 (58 students): 49 PLACED, 5 SHORTLISTED, 2 INTERVIEW, 1 APPLIED, 1 REJECTED (84.5% placement)
        // Admin3 (40 students): 35 PLACED, 3 SHORTLISTED, 1 INTERVIEW, 1 APPLIED (87.5% placement)
        List<Application> apps = new ArrayList<>();

        createApplicationsForStudentList(admin1Students, drives, apps, 75, 4, 2, 1, 0);
        createApplicationsForStudentList(admin2Students, drives, apps, 49, 5, 2, 1, 1);
        createApplicationsForStudentList(admin3Students, drives, apps, 35, 3, 1, 1, 0);

        applicationRepository.saveAll(apps);

        log.info("PlacePro Faculty Placement Cell data initialization complete!");
        log.info("Admin1: {} students | Admin2: {} students | Admin3: {} students",
                admin1Students.size(), admin2Students.size(), admin3Students.size());
    }

    private void createApplicationsForStudentList(List<Student> students, List<PlacementDrive> drives, List<Application> outApps,
                                                    int numPlaced, int numShortlisted, int numInterview, int numApplied, int numRejected) {
        Random rng = new Random(students.size() + numPlaced);
        int total = students.size();

        for (int i = 0; i < total; i++) {
            Student s = students.get(i);
            List<PlacementDrive> eligibleDrives = drives.stream().filter(d -> {
                String desc = d.getCompany().getDescription().toUpperCase();
                String sDept = s.getDepartment().toUpperCase();
                return desc.contains(sDept);
            }).toList();

            PlacementDrive targetDrive = !eligibleDrives.isEmpty()
                    ? eligibleDrives.get(rng.nextInt(eligibleDrives.size()))
                    : drives.get(rng.nextInt(drives.size()));

            String status;
            if (i < numPlaced) status = "PLACED";
            else if (i < numPlaced + numShortlisted) status = "SHORTLISTED";
            else if (i < numPlaced + numShortlisted + numInterview) status = "INTERVIEW";
            else if (i < numPlaced + numShortlisted + numInterview + numApplied) status = "APPLIED";
            else status = "REJECTED";

            Application app = new Application(s, targetDrive, LocalDate.now().minusDays(rng.nextInt(12) + 1), status, "Placement application record updated");
            outApps.add(app);
        }
    }

    private List<Student> generateStudentsForOfficer(String code, String dept, int count, int startIdx, User officer, String baseSkills) {
        String[] firstNames = {"Aarav", "Priya", "Rohan", "Ananya", "Karthik", "Deepika", "Vikram", "Neha", "Siddharth", "Kavya",
                "Aditya", "Meera", "Rahul", "Pooja", "Arjun", "Divya", "Varun", "Ishita", "Yash", "Rhea",
                "Manish", "Tanvi", "Gautam", "Shruti", "Akash", "Anushka", "Nikhil", "Sneha", "Kunal", "Simran",
                "Sanjay", "Swati", "Tarun", "Bhavna", "Harish", "Ritu"};

        String[] lastNames = {"Sharma", "Patel", "Verma", "Reddy", "Nair", "Iyer", "Singh", "Gupta", "Rao", "Joshi",
                "Kumar", "Deshmukh", "Chopra", "Kulkarni", "Mehta", "Bhat", "Saxena", "Pillai", "Menon", "Sen"};

        List<Student> list = new ArrayList<>();
        Random rng = new Random(code.hashCode() + startIdx);

        for (int i = 0; i < count; i++) {
            int num = startIdx + i;
            String regNo = String.format("2024%s%03d", code, num);
            String firstName = firstNames[rng.nextInt(firstNames.length)];
            String lastName = lastNames[rng.nextInt(lastNames.length)];
            String name = firstName + " " + lastName;
            String email = (firstName.toLowerCase() + "." + lastName.toLowerCase() + num + "@gmail.com");
            String phone = String.format("9876%06d", num * 100 + rng.nextInt(90));

            double cgpa = Math.round((6.8 + rng.nextDouble() * 3.0) * 100.0) / 100.0;
            int arrears = (cgpa > 8.0) ? 0 : (rng.nextDouble() > 0.7 ? 1 : 0);
            double apt = Math.round((70.0 + rng.nextDouble() * 28.0) * 10.0) / 10.0;
            double comm = Math.round((72.0 + rng.nextDouble() * 26.0) * 10.0) / 10.0;

            String username = ("student_" + code.toLowerCase() + "_" + num);
            User user = new User(username, passwordEncoder.encode("student123"), "ROLE_STUDENT");

            Student student = new Student(
                    regNo,
                    name,
                    email,
                    phone,
                    dept,
                    2025,
                    cgpa,
                    arrears,
                    baseSkills,
                    "NPTEL Certification, College Coding Honor",
                    dept + " Final Year Capstone Project",
                    apt,
                    comm,
                    user
            );
            student.setAssignedAdmin(officer);
            list.add(student);
        }
        return list;
    }
}
