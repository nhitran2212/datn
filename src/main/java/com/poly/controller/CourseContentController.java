package com.poly.controller;

import com.poly.entity.Course;
import com.poly.entity.Video; // Import Video entity
import com.poly.repository.QuestionRepository;
import com.poly.entity.Question; // Import Question entity
import com.poly.service.CourseService;
import com.poly.service.VideoService; // Import VideoService
import com.poly.service.QuestionService; // Import QuestionService
import com.poly.service.QuizExcelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // For @ModelAttribute
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // For @PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // For RedirectAttributes
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType; // Import MediaType

import java.io.IOException;
import java.util.List; // For List type if not already imported

@Controller
@RequestMapping("/course-content") // Base path for this controller
public class CourseContentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private VideoService videoService; // Autowire VideoService

    @Autowired
    private QuestionService questionService; // Autowire QuestionService

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuizExcelService quizExcelService;


    // --- Course Content Page Display ---
    @GetMapping("/{courseId}") // Maps to /course-content/{courseId}
    public String showCourseContent(@PathVariable("courseId") Long courseId, Model model) {
        Course course = courseService.findById(courseId);

        if (course == null) {
            // Handle case where course is not found, e.g., redirect to an error page
            return "redirect:/Crud_Course?error=courseNotFound";
        }

        model.addAttribute("course", course);
        // Add actual videos and questions related to this course
        model.addAttribute("videos", videoService.findByCourseID_khoa_hoc(courseId));
        model.addAttribute("questions", questionService.findByCourseID_khoa_hoc(courseId));

        // Add empty video/question objects for the "add" modals to bind to
        model.addAttribute("newVideo", new Video());
        model.addAttribute("newQuestion", new Question());

        return "course_content"; // This corresponds to your HTML file: course_content.html
    }

    // --- Video CRUD Operations ---

    // Method to handle adding a new video
    @PostMapping("/{courseId}/videos/add")
    public String addVideo(@PathVariable("courseId") Long courseId,
                           @ModelAttribute Video video,
                           RedirectAttributes redirectAttributes) {
        Course course = courseService.findById(courseId);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/Crud_Course";
        }
        video.setCourse(course); // Link the video to the course
        videoService.save(video);
        redirectAttributes.addFlashAttribute("successMessage", "Video added successfully!");
        return "redirect:/course-content/" + courseId + "#video-tab"; // Redirect back to video tab
    }

    // Method to handle updating an existing video
    @PostMapping("/{courseId}/videos/update")
    public String updateVideo(@PathVariable("courseId") Long courseId,
                              @ModelAttribute Video video,
                              RedirectAttributes redirectAttributes) {
        Course course = courseService.findById(courseId);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/Crud_Course";
        }
        // Basic check: Ensure the video exists and belongs to the correct course
        // For more robust validation, you might fetch and compare more properties
        Video existingVideo = videoService.findById(video.getID_video());
        if (existingVideo == null || !existingVideo.getCourse().getID_khoa_hoc().equals(courseId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Video not found or does not belong to this course.");
            return "redirect:/course-content/" + courseId + "#video-tab";
        }

        video.setCourse(course); // Ensure the course relationship is maintained
        videoService.save(video);
        redirectAttributes.addFlashAttribute("successMessage", "Video updated successfully!");
        return "redirect:/course-content/" + courseId + "#video-tab"; // Redirect back to video tab
    }

    // Method to handle deleting a video
    @GetMapping("/{courseId}/videos/delete/{videoId}")
    public String deleteVideo(@PathVariable("courseId") Long courseId,
                              @PathVariable("videoId") Long videoId,
                              RedirectAttributes redirectAttributes) {
        // Optional: Check if video belongs to course before deleting for security/validation
        Video videoToDelete = videoService.findById(videoId);
        if (videoToDelete == null || !videoToDelete.getCourse().getID_khoa_hoc().equals(courseId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Video not found or does not belong to this course.");
            return "redirect:/course-content/" + courseId + "#video-tab";
        }

        videoService.delete(videoId);
        redirectAttributes.addFlashAttribute("successMessage", "Video deleted successfully!");
        return "redirect:/course-content/" + courseId + "#video-tab"; // Redirect back to video tab
    }

    // --- Question CRUD Operations ---

    // Method to handle adding a new question
    @PostMapping("/{courseId}/questions/add")
    public String addQuestion(@PathVariable("courseId") Long courseId,
                              @ModelAttribute Question question,
                              RedirectAttributes redirectAttributes) {
        Course course = courseService.findById(courseId);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/Crud_Course";
        }
        String correctAnswer = question.getDap_an_dung();
        String answerA = question.getDap_an_a();
        String answerB = question.getDap_an_b();
        String answerC = question.getDap_an_c();
        String answerD = question.getDap_an_d();

        // Check if correct answer matches any of the options
        if (correctAnswer == null ||
            (!correctAnswer.equals(answerA) &&
             !correctAnswer.equals(answerB) &&
             !correctAnswer.equals(answerC) &&
             !correctAnswer.equals(answerD))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đáp án đúng phải trùng khớp với một trong các đáp án A, B, C, D.");
            // Keep the entered data so user doesn't lose it (optional, but good UX)
            redirectAttributes.addFlashAttribute("newQuestion", question);
            return "redirect:/course-content/" + courseId + "#quiz-tab";
        }
        question.setCourse(course); // Link the question to the course
        questionService.save(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question added successfully!");
        return "redirect:/course-content/" + courseId + "#quiz-tab"; // Redirect back to quiz tab
    }

    // Method to handle updating an existing question
    @PostMapping("/{courseId}/questions/update")
    public String updateQuestion(@PathVariable("courseId") Long courseId,
                                 @ModelAttribute Question question,
                                 RedirectAttributes redirectAttributes) {
        Course course = courseService.findById(courseId);
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
            return "redirect:/Crud_Course";
        }
        // Basic check: Ensure the question exists and belongs to the correct course
        Question existingQuestion = questionService.findById(question.getID_cau_hoi());
        if (existingQuestion == null || !existingQuestion.getCourse().getID_khoa_hoc().equals(courseId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Question not found or does not belong to this course.");
            return "redirect:/course-content/" + courseId + "#quiz-tab";
        }
        String correctAnswer = question.getDap_an_dung();
        String answerA = question.getDap_an_a();
        String answerB = question.getDap_an_b();
        String answerC = question.getDap_an_c();
        String answerD = question.getDap_an_d();

        // Check if correct answer matches any of the options
        if (correctAnswer == null ||
            (!correctAnswer.equals(answerA) &&
             !correctAnswer.equals(answerB) &&
             !correctAnswer.equals(answerC) &&
             !correctAnswer.equals(answerD))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đáp án đúng phải trùng khớp với một trong các đáp án A, B, C, D.");
            // Keep the entered data so user doesn't lose it (optional, but good UX)
            redirectAttributes.addFlashAttribute("questionToUpdate", question); // Assuming your update form uses 'questionToUpdate'
            return "redirect:/course-content/" + courseId + "#quiz-tab";
        }
        question.setCourse(course); // Ensure the course relationship is maintained
        questionService.save(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully!");
        return "redirect:/course-content/" + courseId + "#quiz-tab"; // Redirect back to quiz tab
    }

    // Method to handle deleting a question
    @GetMapping("/{courseId}/questions/delete/{questionId}")
    public String deleteQuestion(@PathVariable("courseId") Long courseId,
                                 @PathVariable("questionId") Long questionId,
                                 RedirectAttributes redirectAttributes) {
        // Optional: Check if question belongs to course before deleting
        Question questionToDelete = questionService.findById(questionId);
        if (questionToDelete == null || !questionToDelete.getCourse().getID_khoa_hoc().equals(courseId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Question not found or does not belong to this course.");
            return "redirect:/course-content/" + courseId + "#quiz-tab";
        }

        questionService.delete(questionId);
        redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully!");
        return "redirect:/course-content/" + courseId + "#quiz-tab"; // Redirect back to quiz tab
    }
    //  Excel Import/Export for Quiz Questions

    @PostMapping("/{courseId}/questions/import")
public String importQuizQuestions(@PathVariable Long courseId,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
    if (file.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Vui lòng chọn một file Excel để tải lên.");
        return "redirect:/course-content/" + courseId + "#quiz-tab";
    }
    try {
        // Import questions from Excel
        List<Question> importedQuestions = quizExcelService.importQuestionsFromExcel(file.getInputStream(), courseId);

        // Check if importedQuestions is valid
        if (importedQuestions == null || importedQuestions.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không có câu hỏi nào được import từ file Excel.");
            return "redirect:/course-content/" + courseId + "#quiz-tab";
        }

        // Delete old questions and save new ones
        questionService.deleteOldQuestions(courseId);
        for (Question q : importedQuestions) {
    q.setID_cau_hoi(null);
}
        questionService.saveNewQuestions(importedQuestions);

        redirectAttributes.addFlashAttribute("message", "Đã nhập " + importedQuestions.size() + " câu hỏi từ Excel thành công!");
    } catch (IOException e) {
        redirectAttributes.addFlashAttribute("error", "Lỗi khi đọc file Excel: " + e.getMessage());
    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", "Lỗi dữ liệu: " + e.getMessage());
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi nhập câu hỏi: " + e.getMessage());
        e.printStackTrace(); // Log lỗi để debug
    }
    return "redirect:/course-content/" + courseId + "#quiz-tab";
}

    @GetMapping("/{courseId}/questions/export")
    public ResponseEntity<byte[]> exportQuizQuestions(@PathVariable Long courseId) {
        List<Question> questions = questionRepository.findQuestionsByCourseId(courseId);
        // LOẠI BỎ DÒNG NÀY: model.addAttribute("questions", questions);
        // Vì đây là phương thức trả về file, không phải view.

        try {
            byte[] excelBytes = quizExcelService.exportQuestionsToExcel(questions);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filename = "cau_hoi_quiz_khoa_hoc_" + courseId + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);

        } catch (IOException e) {
            e.printStackTrace(); // Luôn log lỗi trong môi trường thực tế
            return ResponseEntity.status(500).body(null); // Trả về lỗi 500 nếu có vấn đề
        }
    }
}