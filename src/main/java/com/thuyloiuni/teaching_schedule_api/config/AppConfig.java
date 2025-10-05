package com.thuyloiuni.teaching_schedule_api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.thuyloiuni.teaching_schedule_api.dto.LecturerDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;


@Configuration
public class AppConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    // @Bean
    // public ModelMapper modelMapper() {
    //     ModelMapper modelMapper = new ModelMapper();

    //     // Cấu hình mapping từ Lecturer -> LecturerDTO
    //     TypeMap<Lecturer, LecturerDTO> lecturerToDtoMap = modelMapper.createTypeMap(Lecturer.class, LecturerDTO.class);
        
    //     // Thêm mapping cụ thể từ lecturerId -> id
    //     lecturerToDtoMap.addMapping(Lecturer::getLecturerId, LecturerDTO::setId);
    //     lecturerToDtoMap.addMapping(Lecturer::getLecturerCode, LecturerDTO::setLecturerCode);

    //     return modelMapper;
    // }

}
