package com.placepro.service;

import com.placepro.dto.StudentDTO;
import com.placepro.model.Student;
import com.placepro.model.User;
import com.placepro.exception.ResourceNotFoundException;
import com.placepro.repository.StudentRepository;
import com.placepro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<StudentDTO> getStudentsByAssignedAdmin(Long adminId) {
        return studentRepository.findByAssignedAdminId(adminId).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<StudentDTO> getStudentsByAssignedAdminUsername(String username) {
        return studentRepository.findByAssignedAdminUsername(username).stream()
                .map(this::convertToDto)
                .toList();
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return convertToDto(student);
    }

    public StudentDTO getStudentByUsername(String username) {
        Student student = studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + username));
        return convertToDto(student);
    }

    public List<StudentDTO> searchStudents(String query, String dept, Double minCgpa) {
        return searchStudents(query, dept, minCgpa, null);
    }

    public List<StudentDTO> searchStudents(String query, String dept, Double minCgpa, Long adminId) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        String cleanDept = (dept != null && !dept.trim().isEmpty()) ? dept.trim() : null;
        return studentRepository.searchStudents(cleanQuery, cleanDept, minCgpa, adminId).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public StudentDTO createStudent(StudentDTO dto, String creatorUsername) {
        if (studentRepository.findByRegistrationNumber(dto.getRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException("Registration number already registered: " + dto.getRegistrationNumber());
        }
        if (studentRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }

        String pwd = (dto.getPassword() != null && !dto.getPassword().trim().isEmpty())
                ? dto.getPassword() : "student123";
        User user = new User(dto.getRegistrationNumber().toLowerCase(), passwordEncoder.encode(pwd), "ROLE_STUDENT");

        Student student = convertToEntity(dto);
        student.setUser(user);

        if (dto.getAssignedAdminId() != null) {
            userRepository.findById(dto.getAssignedAdminId()).ifPresent(student::setAssignedAdmin);
        } else if (creatorUsername != null) {
            userRepository.findByUsername(creatorUsername).ifPresent(student::setAssignedAdmin);
        }

        Student saved = studentRepository.save(student);
        return convertToDto(saved);
    }

    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setDepartment(dto.getDepartment());
        existing.setCgpa(dto.getCgpa());
        existing.setGraduationYear(dto.getGraduationYear());
        existing.setArrears(dto.getArrears());
        existing.setSkills(dto.getSkills());
        existing.setCertifications(dto.getCertifications());
        existing.setProjects(dto.getProjects());
        existing.setAptitudeScore(dto.getAptitudeScore());
        existing.setCommunicationScore(dto.getCommunicationScore());

        if (dto.getAssignedAdminId() != null) {
            userRepository.findById(dto.getAssignedAdminId()).ifPresent(existing::setAssignedAdmin);
        }

        Student updated = studentRepository.save(existing);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        studentRepository.delete(student);
    }

    public StudentDTO convertToDto(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setRegistrationNumber(student.getRegistrationNumber());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDepartment(student.getDepartment());
        dto.setCgpa(student.getCgpa());
        dto.setGraduationYear(student.getGraduationYear());
        dto.setArrears(student.getArrears());
        dto.setSkills(student.getSkills());
        dto.setCertifications(student.getCertifications());
        dto.setProjects(student.getProjects());
        dto.setAptitudeScore(student.getAptitudeScore());
        dto.setCommunicationScore(student.getCommunicationScore());

        if (student.getAssignedAdmin() != null) {
            dto.setAssignedAdminId(student.getAssignedAdmin().getId());
            dto.setAssignedAdminUsername(student.getAssignedAdmin().getUsername());
        }

        return dto;
    }

    public Student convertToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setId(dto.getId());
        student.setRegistrationNumber(dto.getRegistrationNumber());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDepartment(dto.getDepartment());
        student.setCgpa(dto.getCgpa());
        student.setGraduationYear(dto.getGraduationYear());
        student.setArrears(dto.getArrears() != null ? dto.getArrears() : 0);
        student.setSkills(dto.getSkills());
        student.setCertifications(dto.getCertifications());
        student.setProjects(dto.getProjects());
        student.setAptitudeScore(dto.getAptitudeScore());
        student.setCommunicationScore(dto.getCommunicationScore());

        if (dto.getAssignedAdminId() != null) {
            userRepository.findById(dto.getAssignedAdminId()).ifPresent(student::setAssignedAdmin);
        }

        return student;
    }
}
