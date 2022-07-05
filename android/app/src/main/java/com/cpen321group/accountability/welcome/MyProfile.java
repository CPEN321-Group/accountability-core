package com.cpen321group.accountability.welcome;

public class MyProfile {
    private String firstname;
    private String lastname;
    private String email;
    private int age;
    private String profession;
    private boolean isAccountant;
    private String accountId;

    public MyProfile(String firstName,String lastName,String e_mail,int age,String profession, boolean isAccountant,String accountId){
        this.firstname = firstName;
        this.lastname= lastName;
        this.email=e_mail;
        this.age = age;
        this.profession=profession;
        this.isAccountant = isAccountant;
        this.accountId = accountId;
    }

    public void setFirstname(String firstName) {
        this.firstname = firstName;
    }

    public void setLastname(String lastName) {
        this.lastname = lastName;
    }

    public void setEmail(String e_mail) {
        this.email = e_mail;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setAccountant(boolean accountant) {
        isAccountant = accountant;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getProfession() {
        return profession;
    }

    public String getAccountId() {
        return accountId;
    }

    public boolean getAccountant() {
        return isAccountant;
    }

}