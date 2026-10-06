package com.placepro.service;

import com.placepro.ai.GeminiService;
import com.placepro.ai.LocalRecommendationEngine;
import com.placepro.dto.RecommendationDTO;
import com.placepro.model.Company;
import com.placepro.model.Student;
import com.placepro.repository.CompanyRepository;
import com.placepro.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AIRecommendationService {

    private final LocalRecommendationEngine localEngine;
    private final GeminiService geminiService;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public AIRecommendationService(LocalRecommendationEngine localEngine,
                                   GeminiService geminiService,
                                   StudentRepository studentRepository,
                                   CompanyRepository companyRepository) {
        this.localEngine = localEngine;
        this.geminiService = geminiService;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
    }

    public List<RecommendationDTO> getCompanyRecommendations(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        List<Company> allCompanies = companyRepository.findAll();

        // Department Eligibility Scoping (e.g. CSE students only receive CSE/IT companies)
        String studentDept = student.getDepartment().toLowerCase();
        List<Company> eligibleCompanies = allCompanies.stream().filter(c -> {
            String desc = c.getDescription() != null ? c.getDescription().toLowerCase() : "";
            if (studentDept.contains("computer") || studentDept.contains("information")) {
                return desc.contains("computer science") || desc.contains("information tech") || desc.isEmpty();
            } else if (studentDept.contains("electronics") || studentDept.contains("electrical")) {
                return desc.contains("electronics") || desc.contains("electrical") || desc.isEmpty();
            } else if (studentDept.contains("mechanical") || studentDept.contains("civil")) {
                return desc.contains("mechanical") || desc.contains("civil") || desc.isEmpty();
            }
            return true;
        }).toList();

        return getRecommendations(student, eligibleCompanies);
    }

    public Map<String, Object> assessPlacementReadiness(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        return getPlacementReadiness(student);
    }

    public List<RecommendationDTO> getRecommendations(Student student, List<Company> companies) {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        for (Company company : companies) {
            RecommendationDTO dto = localEngine.evaluateMatch(student, company);

            if (geminiService.isConfigured()) {
                String prompt = buildPromptForCompanyRecommendation(student, company, dto);
                String aiResponse = geminiService.generateExplanation(prompt);
                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    dto.setRecommendationReason(aiResponse);
                    dto.setSource("Gemini AI");
                }
            }

            recommendations.add(dto);
        }

        recommendations.sort(Comparator.comparing(RecommendationDTO::getMatchScore).reversed());
        return recommendations;
    }

    public RecommendationDTO getSkillGapAnalysis(Student student, Company company) {
        RecommendationDTO dto = localEngine.evaluateMatch(student, company);

        if (geminiService.isConfigured()) {
            String prompt = String.format(
                "Perform a skill-gap analysis for student %s applying for role at %s (%s industry).\n" +
                "Student Skills: %s\n" +
                "Company Required Skills: %s\n" +
                "Matched Skills: %s\n" +
                "Missing Skills: %s\n" +
                "Provide a 2-3 sentence actionable skill development roadmap for the student.",
                student.getName(), company.getName(), company.getIndustry(),
                student.getSkills(), company.getRequiredSkills(),
                String.join(", ", dto.getMatchedSkills()),
                String.join(", ", dto.getMissingSkills())
            );

            String aiResponse = geminiService.generateExplanation(prompt);
            if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                dto.setSuggestedImprovements(aiResponse);
                dto.setSource("Gemini AI");
            }
        }

        return dto;
    }

    public Map<String, Object> getPlacementReadiness(Student student) {
        double cgpa = student.getCgpa() != null ? student.getCgpa() : 0.0;
        double aptitude = student.getAptitudeScore() != null ? student.getAptitudeScore() : 0.0;
        double comm = student.getCommunicationScore() != null ? student.getCommunicationScore() : 0.0;
        int certs = student.getCertifications() != null && !student.getCertifications().isEmpty() ? student.getCertifications().split(",").length : 0;
        int projects = student.getProjects() != null && !student.getProjects().isEmpty() ? student.getProjects().split(",").length : 0;

        double overallScore = Math.round(((cgpa / 10.0 * 40.0) + (aptitude / 100.0 * 25.0) + (comm / 100.0 * 20.0) + Math.min(15.0, (certs + projects) * 3.75)) * 10.0) / 10.0;

        String readinessLevel = overallScore >= 82.0 ? "HIGH" : (overallScore >= 68.0 ? "MEDIUM" : "NEEDS_IMPROVEMENT");

        Map<String, Object> result = new HashMap<>();
        result.put("overallScore", overallScore);
        result.put("readinessLevel", readinessLevel);
        result.put("source", geminiService.isConfigured() ? "Gemini AI" : "Placement Analytics Engine");

        if (geminiService.isConfigured()) {
            String prompt = String.format(
                "Assess placement readiness for student %s (%s department).\n" +
                "CGPA: %.2f, Aptitude: %.1f, Comm: %.1f, Certs/Projects Count: %d\n" +
                "Provide a brief 2-sentence summary of candidate placement readiness.",
                student.getName(), student.getDepartment(), cgpa, aptitude, comm, certs + projects
            );
            String explanation = geminiService.generateExplanation(prompt);
            if (explanation != null && !explanation.trim().isEmpty()) {
                result.put("explanation", explanation);
            } else {
                result.put("explanation", generateDefaultExplanation(student, overallScore, readinessLevel));
            }
        } else {
            result.put("explanation", generateDefaultExplanation(student, overallScore, readinessLevel));
        }

        result.put("disclaimer", "Readiness score evaluated across academic CGPA, aptitude test performance, communication skills, and project portfolio.");
        return result;
    }

    private String generateDefaultExplanation(Student student, double score, String level) {
        if ("HIGH".equals(level)) {
            return String.format("%s demonstrates outstanding placement readiness (Score: %.1f/100) with strong academics and technical competencies.", student.getName(), score);
        } else if ("MEDIUM".equals(level)) {
            return String.format("%s has solid foundational skills (Score: %.1f/100). Focus on mock interviews and aptitude practice to achieve top tier placement.", student.getName(), score);
        } else {
            return String.format("%s requires targeted skill enhancement (Score: %.1f/100) in core domain subjects and communication skills.", student.getName(), score);
        }
    }

    private String buildPromptForCompanyRecommendation(Student student, Company company, RecommendationDTO dto) {
        return String.format(
            "Analyze match for student %s (%s) applying to %s (%s).\n" +
            "Student Skills: %s\nCompany Required Skills: %s\n" +
            "Matched: %s, Missing: %s\n" +
            "Provide 2 concise sentences explaining why this student is a good or weak match.",
            student.getName(), student.getDepartment(), company.getName(), company.getIndustry(),
            student.getSkills(), company.getRequiredSkills(),
            String.join(", ", dto.getMatchedSkills()), String.join(", ", dto.getMissingSkills())
        );
    }
}
