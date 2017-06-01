package com.sky.slog.bean;

import com.sky.slog.parse.Parser;

/**
 * 一句话注释。
 * <p>
 * 详细内容。
 *
 * @author sky on 2017/6/1
 */

public class StudentParser implements Parser<Student> {
    @Override
    public Class<Student> getParseType() {
        return Student.class;
    }

    @Override
    public String parseToString(Student student) {
        return student.getName() + " is a " + student.getAge() + " years old " + (student.isBoy() ? "boy" : "girl");
    }
}
