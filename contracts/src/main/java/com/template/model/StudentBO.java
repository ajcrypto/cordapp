package com.template.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.corda.core.serialization.CordaSerializable;

@JsonSerialize
@CordaSerializable
@JsonDeserialize(using = StudentBODeserializer.class)
public class StudentBO implements BusinessObject{

    private String name;
    private String address;
    private String gender;
    private String hobby;
    private String s1Party;
    private String s2Party;

    public StudentBO(String name, String address, String gender, String hobby, String s1Party, String s2Party) {
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.hobby = hobby;
        this.s1Party = s1Party;
        this.s2Party = s2Party;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getS1Party() {
        return s1Party;
    }

    public void setS1Party(String s1Party) {
        this.s1Party = s1Party;
    }

    public String getS2Party() {
        return s2Party;
    }

    public void setS2Party(String s2Party) {
        this.s2Party = s2Party;
    }


}
