package com.placepro.ai;

import com.placepro.dto.RecommendationDTO;
import com.placepro.model.Company;
import com.placepro.model.Student;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LocalRecommendationEngine {

    public RecommendationDTO evaluateMatch(Student student, Company company) {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setCompanyId(company.getId());
        dto.setCompanyName(company.getName());
        dto.setIndustry(company.getIndustry());
        dto.setLocation(company.getLocation());
        dto.setPackageOffered(company.getPackageOffered());
        dto.setSource("Local Fallback Engine");

        List<String> studentSkills = parseCsvList(student.getSkills());
        List<String> companySkills = parseCsvList(company.getRequiredSkills());

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String reqSkill : companySkills) {
            boolean matched = studentSkills.stream()
                    .anyMatch(s -> s.equalsIgnoreCase(reqSkill.trim()));
            if (matched) {
                matchedSkills.add(reqSkill.trim());
            } else {
                missingSkills.add(reqSkill.trim());
            }
        }

        dto.setMatchedSkills(matchedSkills);
        dto.setMissingSkills(missingSkills);

        // 1. Skill Match Score (50%)
        double skillScoreRatio = companySkills.isEmpty() ? 1.0 : (double) matchedSkills.size() / companySkills.size();
        double skillWeightScore = skillScoreRatio * 50.0;

        // 2. CGPA Score (20%) - Ratio against company min CGPA or 10.0 scale
        double studentCgpa = (student.getCgpa() != null) ? student.getCgpa() : 0.0;
        double minCgpa = (company.getMinimumCgpa() != null) ? company.getMinimumCgpa() : 6.0;
        double cgpaRatio = Math.min(1.0, studentCgpa / Math.max(minCgpa, 1.0));
        double cgpaWeightScore = cgpaRatio * 20.0;

        // 3. Aptitude Score (15%) - Student aptitude score out of 100
        double aptitude = (student.getAptitudeScore() != null) ? student.getAptitudeScore() : 70.0;
        double aptitudeWeightScore = (aptitude / 100.0) * 15.0;

        // 4. Communication Score (10%) - Student communication score out of 100
        double comm = (student.getCommunicationScore() != null) ? student.getCommunicationScore() : 70.0;
        double commWeightScore = (comm / 100.0) * 10.0;

        // 5. Certifications & Projects (5%)
        int certCount = parseCsvList(student.getCertifications()).size();
        int projCount = parseCsvList(student.getProjects()).size();
        double certProjRatio = Math.min(1.0, (certCount + projCount) / 4.0);
        double certProjWeightScore = certProjRatio * 5.0;

        double totalMatchScore = Math.round((skillWeightScore + cgpaWeightScore + aptitudeWeightScore + commWeightScore + certProjWeightScore) * 10.0) / 10.0;
        dto.setMatchScore(totalMatchScore);

        // Reason generation
        StringBuilder reason = new StringBuilder();
        reason.append(String.format("Student matches %d of %d required skills. ", matchedSkills.size(), companySkills.size()));
        if (studentCgpa >= minCgpa) {
            reason.append(String.format("CGPA (%.2f) meets the minimum requirement of %.2f. ", studentCgpa, minCgpa));
        } else {
            reason.append(String.format("CGPA (%.2f) is below requirement of %.2f. ", studentCgpa, minCgpa));
        }
        if (student.getArrears() != null && company.getMaximumArrears() != null) {
            if (student.getArrears() <= company.getMaximumArrears()) {
                reason.append(String.format("Arrears count (%d) is within limit (%d). ", student.getArrears(), company.getMaximumArrears()));
            } else {
                reason.append(String.format("Arrears count (%d) exceeds company limit (%d). ", student.getArrears(), company.getMaximumArrears()));
            }
        }
        dto.setRecommendationReason(reason.toString());

        // Improvements
        if (!missingSkills.isEmpty()) {
            dto.setSuggestedImprovements("Focus on acquiring key missing technical skills: " + String.join(", ", missingSkills) + ".");
        } else {
            dto.setSuggestedImprovements("Excellent technical match! Prepare for coding assessments and mock interviews.");
        }

        return dto;
    }

    private List<String> parseCsvList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
