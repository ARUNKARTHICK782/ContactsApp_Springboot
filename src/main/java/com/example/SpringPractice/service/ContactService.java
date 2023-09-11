package com.example.SpringPractice.service;

import com.example.SpringPractice.dao.ContactDao;
import com.example.SpringPractice.model.Contact;
import com.example.SpringPractice.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@Service
public class ContactService {
    private final ContactDao contactDao;

    @Autowired
    public ContactService(@Qualifier("contactDao") ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void addContact(Contact contact) throws CustomException {
         contactDao.addContact(contact);
    }

    public void addNumberToContact(String name, List<HashMap<String,String>> mobileNumberWithTags) throws CustomException{
        contactDao.addNumberToContact(name,mobileNumberWithTags);
    }

    public List<Contact> showContacts(){
        return contactDao.showContacts();
    }

    public Contact deleteContact(String name) throws  CustomException{
        return contactDao.deleteContact(name);
    }

    public Contact deletePhoneNumberFromContact(String phNo) throws CustomException{
        return contactDao.deletePhoneNumberFromContact(phNo);
    }

    public Contact updateContact(String name,Contact contact) throws CustomException{
        return contactDao.updateContact(name,contact);
    }

    public Contact getContact(String name) throws CustomException{
        return contactDao.getContact(name);
    }

    public void exportContacts() throws CustomException, IOException {
        contactDao.exportContacts();
    }

    public void importContacts(MultipartFile file) throws CustomException {
        contactDao.importContacts(file);
    }
}
