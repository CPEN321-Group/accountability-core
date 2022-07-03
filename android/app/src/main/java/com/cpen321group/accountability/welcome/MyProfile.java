package com.cpen321group.accountability.welcome;

public class MyProfile {
    private String firstName;
    private String lastName;
    private String e_mail;
    private int age;
    private String profession;

    public MyProfile(String firstName,String lastName,String e_mail,int age,String profession){
        this.firstName = firstName;
        this.lastName= lastName;
        this.e_mail=e_mail;
        this.age = age;
        this.profession=profession;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getE_mail() {
        return e_mail;
    }

    public int getAge() {
        return age;
    }

    public String getProfession() {
        return profession;
    }
}