package com.example.SpringPractice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Contact {
    private String name;
    private List<HashMap<String,String>> mobileNumberWithTags = new ArrayList<>();

//    public Contact(String name,String tag,String phNo) {
//        this.name = name;
//        HashMap<String,String> phoneNumberWithTag = new HashMap<>();
//        phoneNumberWithTag.put(tag,phNo);
//        mobileNumberWithTags.add(phoneNumberWithTag);
//    }

    public Contact(String name,List<HashMap<String,String>> phoneNumberWithTags){
        this.name = name;
        mobileNumberWithTags = phoneNumberWithTags;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HashMap<String, String>> getMobileNumberWithTags() {
        return mobileNumberWithTags;
    }

    public void setMobileNumberWithTags(List<HashMap<String, String>> mobileNumberWithTags) {
        this.mobileNumberWithTags = mobileNumberWithTags;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", mobileNumberWithTags=" + mobileNumberWithTags +
                '}';
    }
}
