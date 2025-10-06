package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {

    private Integer id;
    private String subjectCode; // Mã môn học, ví dụ: IT001
    private String subjectName; // Tên môn học, ví dụ: Lập trình hướng đối tượng
    private int credits;    // Số tín chỉ

}