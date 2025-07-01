package com.poly.service;

import com.poly.entity.Video;
import com.poly.repository.VideoRepository;
import com.poly.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImp implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    @Override
    public Video findById(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    @Override
    public Video save(Video video) {
        return videoRepository.save(video);
    }

    @Override
    public void delete(Long id) {
        videoRepository.deleteById(id);
    }

    @Override
    public List<Video> findByCourseID_khoa_hoc(Long courseId) {
        // CALL THE CORRECTED REPOSITORY METHOD HERE
        return videoRepository.findVideosByCourseId(courseId);
    }
}