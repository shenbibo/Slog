package com.sky.slog.bean;

/**
 * 一句话注释。
 * <p>
 * 详细内容。
 *
 * @author sky on 2017/6/1
 */

public class Student {
    private int number;
    private int age;
    private String name;
    private boolean isBoy;

    public Student(int number, int age, String name, boolean isBoy){
        this.number = number;
        this.age = age;
        this.name = name;
        this.isBoy = isBoy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public boolean isBoy() {
        return isBoy;
    }

    public void setBoy(boolean boy) {
        isBoy = boy;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
