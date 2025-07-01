package com.poly.controller;

import com.poly.dto.QuizSubmissionDTO;
import com.poly.entity.Course;
import com.poly.entity.Enrollment;
import com.poly.entity.Question;
import com.poly.entity.User;
import com.poly.entity.Video;
import com.poly.service.CourseService;
import com.poly.service.EnrollmentService;
import com.poly.service.QuestionService;
import com.poly.service.VideoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.http.HttpHeaders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class VideoViewController {

    private static final Logger log = LoggerFactory.getLogger(VideoViewController.class);

    @Autowired
    private CourseService courseService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/video-learning/{courseId}")
    public String viewCourseVideos(@PathVariable("courseId") Long courseId, Model model) {
        log.info("Accessing videos for courseId: {}", courseId);
        Course course = courseService.findById(courseId);
        if (course == null) {
            log.warn("Course not found for courseId: {}", courseId);
            return "redirect:/index";
        }

        List<Video> videos = videoService.findByCourseID_khoa_hoc(courseId);
        if (videos.isEmpty()) {
            log.warn("No videos found for courseId: {}", courseId);
            model.addAttribute("errorMessage", "Khóa học này chưa có video nào.");
            return "redirect:/course-content/" + courseId;
        }

        Video currentVideo = videos.get(0);

        model.addAttribute("course", course);
        model.addAttribute("videos", videos);
        model.addAttribute("currentVideo", currentVideo);
        model.addAttribute("isAIChatExpanded", false);

        return "Video_Learning";
    }

    @GetMapping("/video-learning/{courseId}/{videoId}")
    public String viewSpecificVideo(@PathVariable("courseId") Long courseId, 
                                   @PathVariable("videoId") Long videoId, 
                                   Model model) {
        log.info("Accessing videoId: {} for courseId: {}", videoId, courseId);
        Course course = courseService.findById(courseId);
        if (course == null) {
            log.warn("Course not found for courseId: {}", courseId);
            return "redirect:/index";
        }

        List<Video> videos = videoService.findByCourseID_khoa_hoc(courseId);
        if (videos.isEmpty()) {
            log.warn("No videos found for courseId: {}", courseId);
            model.addAttribute("errorMessage", "Khóa học này chưa có video nào.");
            return "redirect:/course-content/" + courseId;
        }

        Video currentVideo = videoService.findById(videoId);
        if (currentVideo == null || !currentVideo.getCourse().getID_khoa_hoc().equals(courseId)) {
            log.warn("Invalid videoId: {} for courseId: {}, defaulting to first video", videoId, courseId);
            currentVideo = videos.get(0);
        }

        model.addAttribute("course", course);
        model.addAttribute("videos", videos);
        model.addAttribute("currentVideo", currentVideo);
        model.addAttribute("isAIChatExpanded", false);

        return "Video_Learning";
    }

    @GetMapping({"/video-learning/quiz/{courseId}", "/quiz/{courseId}"})
    public String showQuiz(@PathVariable("courseId") Long courseId, Model model, HttpSession session) {
        log.info("Accessing quiz for courseId: {}", courseId);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            log.warn("User not logged in, redirecting to login");
            return "redirect:/login";
        }

        if (courseId == null || courseId <= 0) {
            log.warn("Invalid courseId: {}, redirecting to index", courseId);
            model.addAttribute("errorMessage", "Khóa học không hợp lệ.");
            return "redirect:/index";
        }

        Course course = courseService.findById(courseId);
        if (course == null) {
            log.warn("Course not found for courseId: {}, redirecting to index", courseId);
            model.addAttribute("errorMessage", "Khóa học không tồn tại.");
            return "redirect:/index";
        }

        List<Question> questions = questionService.findByCourseID_khoa_hoc(courseId);
        model.addAttribute("course", course);
        if (questions != null && !questions.isEmpty()) {
            model.addAttribute("questions", questions);
            model.addAttribute("currentQuestion", questions.get(0));
        } else {
            log.warn("No questions found for courseId: {}", courseId);
            model.addAttribute("errorMessage", "Chưa có câu hỏi cho khóa học này.");
            model.addAttribute("questions", List.of());
            model.addAttribute("currentQuestion", null);
        }

        int totalTimeInSeconds = (course.getDiem_dat() != null && course.getDiem_dat() > 0) ? (int) (course.getDiem_dat() * 60) : 600;
        model.addAttribute("totalTimeInSeconds", totalTimeInSeconds);

        return "Quiz";
    }

    @PostMapping("/submit-quiz")
    @ResponseBody
    public Map<String, Object> submitQuiz(@RequestBody QuizSubmissionDTO dto, HttpSession session) {
        log.info("Received submit-quiz request at {}: DTO - courseId: {}, answers: {}, timeLeft: {}, full DTO: {}", 
                 new java.util.Date(), dto != null ? dto.getCourseId() : null, 
                 dto != null ? dto.getAnswers() : null, 
                 dto != null ? dto.getTimeLeft() : null, dto);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            log.error("User not logged in for submit-quiz");
            throw new IllegalArgumentException("Please log in to submit the quiz");
        }

        if (dto == null) {
            log.error("DTO is null in submit-quiz request");
            throw new IllegalArgumentException("Request body cannot be null");
        }

        Long courseId = dto.getCourseId();
        if (courseId == null) {
            log.warn("CourseId is null, using default value 1 for testing. Full DTO: {}", dto);
            courseId = 1L;
        } else if (courseId <= 0) {
            log.error("CourseId is invalid (value: {}) in submit-quiz request. Full DTO: {}", courseId, dto);
            throw new IllegalArgumentException("Course ID cannot be zero or negative");
        }

        log.debug("Fetching course for courseId: {}", courseId);
        Course course = courseService.findById(courseId);
        if (course == null) {
            log.error("Course not found for courseId: {}", courseId);
            throw new IllegalArgumentException("Course not found");
        }

        log.debug("Fetching questions for courseId: {}", courseId);
        List<Question> questions = questionService.findByCourseID_khoa_hoc(courseId);
        if (questions == null || questions.isEmpty()) {
            log.error("No questions found for courseId: {}", courseId);
            throw new IllegalArgumentException("No questions available");
        }

        Map<String, String> answers = dto.getAnswers();
        int timeLeft = dto.getTimeLeft();

        double score = 0;
        int totalQuestions = questions.size();
        log.debug("Calculating score with {} answers for {} questions", answers != null ? answers.size() : 0, totalQuestions);

        if (answers != null) {
            for (int i = 0; i < totalQuestions; i++) {
                Question q = questions.get(i);
                String userAnswer = answers.get(String.valueOf(i));
                log.debug("Question {} (ID_cau_hoi: {}): userAnswer = {}, dap_an_dung = {}", i, q.getID_cau_hoi(), userAnswer, q.getDap_an_dung());
                if (userAnswer != null && userAnswer.trim().equalsIgnoreCase(q.getDap_an_dung().trim())) {
                    score++;
                    log.debug("Match found for question {} (ID_cau_hoi: {})", i, q.getID_cau_hoi());
                }
            }
        }
        score = (score / totalQuestions) * 100;
        log.debug("Calculated score: {}", score);

        // Lưu kết quả vào enrollment
        if (user != null) {
            log.debug("Fetching or creating enrollment for userId: {}, courseId: {}", user.getIdNguoiDung(), courseId);
            Enrollment enrollment = enrollmentService.findByUserAndCourse(user.getIdNguoiDung(), courseId);
            if (enrollment == null) {
                enrollment = new Enrollment();
                enrollment.setUser(user);
                enrollment.setCourse(course);
                enrollment.setPrice(course.getGia_tien());
                enrollment.setEnrollmentDate(LocalDateTime.now());
            }
            enrollment.setDiem(score);
            if (score >= course.getDiem_dat()) {
                enrollment.setFinishDate(LocalDate.now());
            }
            enrollmentService.save(enrollment);
            log.info("Saved enrollment for user {} with score {}", user.getIdNguoiDung(), score);
        } else {
            log.warn("No user in session after initial check");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("pass", score >= course.getDiem_dat());
        response.put("totalQuestions", totalQuestions); // Thêm để hiển thị số câu hỏi
        response.put("correctAnswers", (int) score); // Số câu đúng
        log.info("Returning response: {}", response);
        return response;
    }
}