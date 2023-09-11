package com.example.SpringPractice.dao;

import com.example.SpringPractice.api.ContactController;
import com.example.SpringPractice.model.Contact;
import com.example.SpringPractice.model.CustomException;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;


@Repository("contactDao")
public class ContactDataAccessService implements ContactDao{
    private static final List<Contact> contacts = new ArrayList<>();

    synchronized boolean  isExists(String name){
        for(Contact contactCounter:contacts){
            if(contactCounter.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }



    boolean isPhoneNumberAlreadyExist(String phNo){
        for(Contact contactCounter : contacts){
            for(HashMap<String,String> phoneNumber : contactCounter.getMobileNumberWithTags()){
                if(phoneNumber.containsValue(phNo)){
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasCommonNumbers(List<HashMap<String, String>> list1, List<HashMap<String, String>> list2) {
        for (HashMap<String, String> map1 : list1) {
            for (HashMap<String, String> map2 : list2) {
                for (String value1 : map1.values()) {
                    for (String value2 : map2.values()) {
                        if (value1 != null && value1.equals(value2)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    @Override
    public synchronized void addContact(Contact contact) throws CustomException {
        if(isExists(contact.getName())){
            throw new CustomException("Contact already exist",409);
        }
//        System.out.println("Adding contact : "+contact);
        contacts.add(contact);
//
//        List<String> alreadyExistingNumbers = new ArrayList<>();
//        boolean sendException = false;
//        List<HashMap<String,String>> uniqueNumbers = new ArrayList<>();
//        for(HashMap<String,String> phoneNumber : contact.getMobileNumberWithTags()){
//            List<String> values = new ArrayList<>(phoneNumber.values());
//            for(String phNo : values) {
//                if (isPhoneNumberAlreadyExist(phNo)) {
//                    sendException = true;
//                    alreadyExistingNumbers.add(phNo);
//                }
//                else{
//                    uniqueNumbers.add(phoneNumber);
//                }
//            }
//        }
//
//        if(!uniqueNumbers.isEmpty()){
//
//        }
    }





    @Override
    public void addNumberToContact(String name,List<HashMap<String,String>> mobileNumberWithTags) throws CustomException {

        if(!isExists(name)){
            throw new CustomException("Contact didn't exist",404);
        }
        for (int contactCounter=0;contactCounter<contacts.size();contactCounter++) {
            if (contacts.get(contactCounter).getName().equalsIgnoreCase(name)) {
                var res =  contacts.get(contactCounter).getMobileNumberWithTags();
                if(hasCommonNumbers(res,mobileNumberWithTags)){
                    throw new CustomException("Some contacts already exists",409);
                }
                res.addAll(mobileNumberWithTags);
                contacts.get(contactCounter).setMobileNumberWithTags(res);
                break;
            }
        }
//        List<String> alreadyExistingNumbers = new ArrayList<>();
//
//        for(HashMap<String,String> phoneNumber : mobileNumberWithTags){
//            List<String> values = new ArrayList<>(phoneNumber.values());
//            for(String phNo : values){
//                if(isPhoneNumberAlreadyExist(phNo)){
//                    sendException = true;
//                    alreadyExistingNumbers.add(phNo);
//                }
//                else{
//                    phoneNumbers.add(phoneNumber);
//                }
//            }
//        }
//
//        if(!phoneNumbers.isEmpty()){
//            contact.setMobileNumberWithTags(phoneNumbers);
//        }
//
//        if(sendException){
//            throw new CustomException(alreadyExistingNumbers+" Phone number already exist",409);
//        }

    }

    @Override
    public List<Contact> showContacts() {

        return contacts;
    }

    @Override
    public Contact deleteContact(String name) throws CustomException {
        if(!isExists(name)){
            throw new CustomException("Phone number didn't exist",404);
        }
        int contactCounter;
        Contact deletedContact = null;
        for(contactCounter = 0;contactCounter<contacts.size();contactCounter++){
            if(contacts.get(contactCounter).getName().equalsIgnoreCase(name)){
                deletedContact = contacts.get(contactCounter);
                contacts.remove(contactCounter);
                break;
            }
        }
        return deletedContact;
    }

    @Override
    public Contact deletePhoneNumberFromContact(String phno) throws CustomException {
        if(!isPhoneNumberAlreadyExist(phno)){
            throw new CustomException("Phone number didn't exists",404);
        }
        boolean numberDeleted = false;
        int contactCounter;
        Contact deletedContact = null;
        for(contactCounter = 0;contactCounter<contacts.size();contactCounter++){
            List<HashMap<String,String>> phoneNumbers = contacts.get(contactCounter).getMobileNumberWithTags();
            for(int phNoCounter = 0;phNoCounter<phoneNumbers.size();phNoCounter++){
                List<String> values = new ArrayList<>(phoneNumbers.get(phNoCounter).values());
                for(String phoneNum : values){
                    if(phoneNum.equals(phno)){
                        deletedContact = contacts.get(contactCounter);
                        phoneNumbers.remove(phNoCounter);
                        numberDeleted = true;
                        break;
                    }
                }
                if(numberDeleted)
                    break;
            }
            if(numberDeleted)
                break;
        }
        return deletedContact;
    }




    boolean validatePhoneNumber(String phNo){
        return phNo.length() == 10;
    }

    @Override
    public Contact updateContact(String name,Contact contact) throws CustomException{
        System.out.println(name+contact);
        if (!isExists(name)) {
            throw new CustomException("Contact didn't exist", 404);
        }
        int contactCounter;
        for (contactCounter = 0;contactCounter < contacts.size();contactCounter++) {
            if (contacts.get(contactCounter).getName().equalsIgnoreCase(name)) {
                if(contact.getName() != null){
                   if(!name.equalsIgnoreCase(contact.getName())){
                       if(isExists(contact.getName())){
                           throw new CustomException("Contact already exists", 409);
                       }
                       contacts.get(contactCounter).setName(contact.getName());
                   }
                }
                if(contact.getMobileNumberWithTags().get(0).containsValue(null)){
                    throw new CustomException("Contact details required",400);
                }else{
                    if(!ContactController.isThereDuplicateContacts(contact.getMobileNumberWithTags())){
                        throw new CustomException("Duplicate contacts found",400);
                    }
                    for(HashMap<String,String> phoneNumber : contact.getMobileNumberWithTags()){
                        List<String> values = new ArrayList<>(phoneNumber.values());
                        for(String phNo : values){
                            if(!validatePhoneNumber(phNo)){
                                throw new CustomException("Invalid phone number",400);
                            }
                        }
                    }
                    contacts.get(contactCounter).setMobileNumberWithTags(contact.getMobileNumberWithTags());
                }
                break;
            }
        }
//        boolean updateName = false;
//        boolean updateNumbers = false;
//
//        if(contact.getName() == null){
//            if(contact.getMobileNumberWithTags().get(0).containsValue(null)){
//                throw new CustomException("Contact details required",400);
//            }else{
//                updateNumbers = true;
//            }
//        }
//
//        else{
//            if(contact.getName().isBlank() || contact.getName().isEmpty()){
//                throw new CustomException("Contact name required",400);
//            }
//            if(!contact.getName().equalsIgnoreCase(name)) {
//                if(isAvailable(contact.getName())){
//                    throw new CustomException("Given name already exists",409);
//                }
//            }
//            if(contact.getMobileNumberWithTags().get(0).containsValue(null)){
//                updateName = true;
//            }else{
//                updateNumbers = true;
//            }
//        }
//
//        if(!ContactController.isThereDuplicateContacts(contact.getMobileNumberWithTags())){
//            throw new CustomException("Duplicate contacts found",400);
//        }
//
//
//        if(!updateName){
//            for(HashMap<String,String> phoneNumber : contact.getMobileNumberWithTags()){
//                List<String> values = new ArrayList<>(phoneNumber.values());
//                for(String phNo : values){
//                    if(!validatePhoneNumber(phNo)){
//                        throw new CustomException("Invalid phone number",400);
//                    }
//                }
//            }
//        }
//
//
        return contacts.get(contactCounter);
    }

    @Override
    public Contact getContact(String name) throws CustomException{
        if(!isExists(name)){
            throw new CustomException("Contact didn't exist",404);
        }
        Contact contactResponse = null;
        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(name)) {
                contactResponse = contact;
            }
        }
        return contactResponse;
    }

    @Override
    public void exportContacts() throws CustomException, IOException {
        long start = System.currentTimeMillis();
        FileWriter writer = new FileWriter("contacts.csv");
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        StringBuffer data = new StringBuffer();
        for (Contact contact : contacts) {
            data.append(contact.getName()).append(",");
            for (HashMap<String, String> phoneNumber : contact.getMobileNumberWithTags()) {
                for (Map.Entry<String, String> phNo : phoneNumber.entrySet()) {
                    data.append(phNo.getKey()).append(",").append(phNo.getValue()).append(",");
                }
            }
            data.append("\n");
        }
        bufferedWriter.write(String.valueOf(data));
        long end = System.currentTimeMillis();
        bufferedWriter.close();
    }


    @Override
    public void importContacts(MultipartFile file) throws CustomException {
        long start = System.currentTimeMillis();
        writeToListUsingExecutors(file);
        long end = System.currentTimeMillis();
}

private Contact sample(String finalLine){
//    System.out.println(Thread.currentThread().getName());
    String [] dataArr = finalLine.replaceAll(",+$", "").split(",");
    List<HashMap<String, String>> phoneNumbersWithTags = new ArrayList<>();
    for (int dataPtr = 1; dataPtr < dataArr.length; dataPtr += 2) {
        HashMap<String, String> phoneNumber = new HashMap<>();
        phoneNumber.put(dataArr[dataPtr], dataArr[dataPtr + 1]);
        phoneNumbersWithTags.add(phoneNumber);
    }
    Contact contact = new Contact(dataArr[0], phoneNumbersWithTags);
    try{
        addContact(contact);
        return contact;
    }catch (CustomException e){
        System.out.println(e.getMessage());
        return null;
    }
}

private void writeToListUsingExecutors(MultipartFile file) throws CustomException {
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    try{
        BufferedReader read = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        while ((line = read.readLine())!= null) {
            String finalLine = line;
            Future<?> result =  exec.submit(()->sample(finalLine));
        }
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }catch (Exception e){
        throw new CustomException(e.getMessage(),500);
    }
}


private void writeToList(MultipartFile file) throws CustomException {
    ThreadPoolExecutor exec = new ThreadPoolExecutor(5,10,20,TimeUnit.SECONDS,new ArrayBlockingQueue<>(21000));
    try{
        BufferedReader read = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        while ((line = read.readLine())!= null) {
            String finalLine = line;
            exec.execute(()->{
//                System.out.println(Thread.currentThread().getName());
                String [] dataArr = finalLine.replaceAll(",+$", "").split(",");
                List<HashMap<String, String>> phoneNumbersWithTags = new ArrayList<>();
                for (int dataPtr = 1; dataPtr < dataArr.length; dataPtr += 2) {
                    HashMap<String, String> phoneNumber = new HashMap<>();
                    phoneNumber.put(dataArr[dataPtr], dataArr[dataPtr + 1]);
                    phoneNumbersWithTags.add(phoneNumber);
                }
                Contact contact = new Contact(dataArr[0], phoneNumbersWithTags);
                try{
                    addContact(contact);
                }catch (CustomException e){
                    System.out.println(e.getMessage());
                }
            });
        }
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }catch (Exception e){
        throw new CustomException(e.getMessage(),500);
    }
}
}

