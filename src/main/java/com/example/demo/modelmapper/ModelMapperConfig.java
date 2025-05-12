package com.example.demo.modelmapper;

import com.example.demo.dtos.task.TaskDto;
import com.example.demo.entities.TaskEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


}
