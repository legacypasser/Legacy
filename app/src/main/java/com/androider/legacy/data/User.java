package com.androider.legacy.data;

/**
 * Created by Think on 2015/4/16.
 */
public class User {
    public int id;
    public String nickname;
    public String email;
    public String school;
    public String major;

    public User(int id, String nickname, String email, String school, String major) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.school = school;
        this.major = major;
    }


}
