package com.example.tutorial;

public class User {
    public String userid,name,age,email, district,subdistrict, like,dislike,rate,pimg,flag;

    public User(){

    }

    public User(String userid,String name,String age,String email,String district,String subdistrict,String pimg,String flag){
        this.userid=userid;
        this.name=name;
        this.age=age;
        this.email=email;
        this.district=district;
        this.subdistrict=subdistrict;
        this.pimg=pimg;
        this.flag=flag;
    }

}
