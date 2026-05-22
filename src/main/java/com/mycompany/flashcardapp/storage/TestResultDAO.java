package com.mycompany.flashcardapp.storage;

import com.mycompany.flashcardapp.model.TestResult;

import java.util.List;
import java.util.stream.Collectors;

public class TestResultDAO {
    private static final String FILE_NAME = "test_results.bin";

    public TestResultDAO() {
        FileDataManager.loadList(FILE_NAME);
    }

    private List<TestResult> getResults() {
        return FileDataManager.loadList(FILE_NAME);
    }

    private void saveResults(List<TestResult> results) {
        FileDataManager.saveList(FILE_NAME, results);
    }

    private int getNextId(List<TestResult> results) {
        return results.stream().mapToInt(TestResult::getId).max().orElse(0) + 1;
    }

    public boolean saveResult(TestResult result) {
        List<TestResult> results = getResults();
        result.setId(getNextId(results));
        results.add(result);
        saveResults(results);
        return true;
    }

    public List<TestResult> getResultsByUser(int userId) {
        return getResults().stream()
                .filter(r -> r.getUserId() == userId)
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt())) // DESC
                .collect(Collectors.toList());
    }

    public double getAverageScore(int userId) {
        List<TestResult> userResults = getResultsByUser(userId);
        if (userResults.isEmpty()) return -1;
        
        double sum = userResults.stream().mapToDouble(TestResult::getPercentage).sum();
        return sum / userResults.size();
    }
}
