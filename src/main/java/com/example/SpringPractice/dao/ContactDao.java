package com.example.SpringPractice.dao;

import com.example.SpringPractice.model.Contact;
import com.example.SpringPractice.model.CustomException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface ContactDao {
    void addContact(Contact contact) throws CustomException;
    void addNumberToContact(String name, List<HashMap<String,String>> mobileNumberWithTags) throws CustomException;
    List<Contact> showContacts();
    Contact deletePhoneNumberFromContact(String phno) throws CustomException;
    Contact deleteContact(String name) throws CustomException;
    Contact updateContact(String phno,Contact contact) throws CustomException;
    Contact getContact(String phno) throws CustomException;
    void exportContacts() throws CustomException, IOException;
    void importContacts(MultipartFile file) throws CustomException;
}
