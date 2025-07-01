package com.poly.service;

import com.poly.entity.Question;
import java.util.List;

public interface QuestionService {
    List<Question> findAll();
    Question findById(Long id);
    Question save(Question question);
    void delete(Long id);
    void deleteByCourseId(Long courseId); // Method to delete questions by course ID
    void saveAll(List<Question> questions); // Method to save a list of questions
    List<Question> findByCourseID_khoa_hoc(Long courseId); // Method to fetch questions for a specific course
    void deleteOldQuestions(Long courseId); // Method to delete old questions for a course
    void saveNewQuestions(List<Question> questions);
    // Method to save new questions for a course
    
}