package com.poly.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class QuizSubmissionDTO {
    @JsonProperty("courseId")
    private Long courseId;

    @JsonProperty("answers")
    private Map<String, String> answers;

    @JsonProperty("timeLeft")
    private int timeLeft;
}