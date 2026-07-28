package com.example.demo.service;

import com.example.demo.entity.Cast;
import com.example.demo.repository.CastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CastService {

    private final CastRepository castRepository;

    public List<Cast> getallActors(){
        return castRepository.findAll();
    }

    public Cast saveActor(Cast newActor){
        return castRepository.save(newActor);
    }
}
