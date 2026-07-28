package com.example.demo.service;

import com.example.demo.entity.Content;
import com.example.demo.repository.CastRepository;
import com.example.demo.repository.ContentRepository;
import com.example.demo.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final MetadataRepository metadataRepository;
    private final CastRepository castRepository;

    public List<Content> getAllContents(){
        return contentRepository.findAll();
    }

    public Content saveContent(Content newContent){
        return contentRepository.save(newContent);
    }
}
