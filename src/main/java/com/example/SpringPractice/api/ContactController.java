package com.example.SpringPractice.api;
import com.example.SpringPractice.model.Contact;
import com.example.SpringPractice.model.CustomException;
import com.example.SpringPractice.model.ResponseMessage;
import com.example.SpringPractice.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;



record AddNumberToContact(List<HashMap<String,String>> mobileNumberWithTags){}



@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("api/v1/contacts")
@RestController
public class ContactController {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

   boolean validatePhoneNumber(String phNo){
       return phNo.length() == 10;
   }

   public static boolean isThereDuplicateContacts(List<HashMap<String,String>> phoneNumbers){
       HashSet<String> numbers = new HashSet<>();
       for(HashMap<String,String> phoneNumber : phoneNumbers){
           numbers.add(phoneNumber.values().toString());
       }
       return numbers.size() == phoneNumbers.size();
   }

//   ResponseMessage validateContact(Contact contact,String target){
//
//        if(contact == null){
//            return new ResponseMessage(false,"Contact details is required",400,null);
//        }
//
//        if(contact.getPhNo() == null || contact.getPhNo().isBlank() || contact.getPhNo().isEmpty()){
//            return new ResponseMessage(false, "Phone number is required", 400, null);
//        }
//
//
//       switch (target) {
//           case "home" -> {
//               contact.setHome(contact.getPhNo());
//               if (contact.getHome() != null) {
//                   if (validatePhoneNumber(contact.getHome())) {
//                       return null;
//                   } else {
//                       return new ResponseMessage(false, "Invalid phone number", 403, null);
//                   }
//               } else {
//                   return new ResponseMessage(false, "Phone number is required", 400, null);
//               }
//           }
//           case "work" -> {
//               contact.setWork(contact.getPhNo());
//               if (contact.getWork().isBlank()) {
//                   if (validatePhoneNumber(contact.getWork())) {
//                       return null;
//                   } else {
//                       return new ResponseMessage(false, "Invalid phone number", 403, null);
//                   }
//               } else {
//                   return new ResponseMessage(false, "Phone number is required", 400, null);
//               }
//           }
//           case "mobile" -> {
//               contact.setMobile(contact.getPhNo());
//               if (contact.getMobile() != null) {
//                   if (validatePhoneNumber(contact.getMobile())) {
//                       return null;
//                   } else {
//                       return new ResponseMessage(false, "Invalid phone number", 403, null);
//                   }
//               } else {
//                   return new ResponseMessage(false, "Phone number is required", 400, null);
//               }
//           }
//       }
//
//        if(contact.getName() == null || contact.getName().isEmpty()){
//            return new ResponseMessage(false,"Contact name is required",400,null);
//        }
//        return null;
//    }

    ResponseMessage validateContactFormat(Contact contact){
        if(contact == null){
            return new ResponseMessage(false,"Contact details required",400,null);
        }
        if(contact.getName() == null){
            return new ResponseMessage(false,"Contact name required",400,null);
        }
        if(contact.getName().isEmpty() || contact.getName().isBlank()) {
            return new ResponseMessage(false, "Contact name required", 400, null);
        }

        if(contact.getMobileNumberWithTags().size() == 0){
            return new ResponseMessage(false,"Contact name required",400,null);
        }

        for(HashMap<String,String> phoneNumber : contact.getMobileNumberWithTags()){
            List<String> values = new ArrayList<>(phoneNumber.values());
            for(String phNo : values){
                if(!validatePhoneNumber(phNo)){
                    return new ResponseMessage(false,"Invalid phone number",400,null);
                }
            }
        }

        if(!isThereDuplicateContacts(contact.getMobileNumberWithTags())){
            return new ResponseMessage(false,"Duplicate numbers detected",409,null);
        }
        return null;
    }


    @PostMapping()
    public ResponseEntity<ResponseMessage> addContact(@RequestBody Contact contact) throws CustomException{
        ResponseMessage responseMessage = validateContactFormat(contact);
        if(responseMessage != null){
            return ResponseEntity.status(400).body(responseMessage);
        }
        try{
            contactService.addContact(contact);
            return ResponseEntity.status(201).body(new ResponseMessage(true,"Added successfully",201,null));
        }
        catch (CustomException e){
            return ResponseEntity.status(409).body(new ResponseMessage(false,e.getMessage(),409,null));
        }

    }

    @PostMapping(path = "{name}")
    public ResponseEntity<ResponseMessage> addNumberToContact(@PathVariable("name") String name,@RequestBody AddNumberToContact addNumberToContact){
        if(addNumberToContact.mobileNumberWithTags().get(0).containsValue(null)){
            return ResponseEntity.status(400).body(new ResponseMessage(false,"Mobile numbers required",400,null));
        }
        if(!isThereDuplicateContacts(addNumberToContact.mobileNumberWithTags())){
            return ResponseEntity.status(409).body(new ResponseMessage(false,"Duplicate numbers detected",409,null));
        }
        try{
            contactService.addNumberToContact(name,addNumberToContact.mobileNumberWithTags());
            return ResponseEntity.status(200).body(new ResponseMessage(true,"Number added to contact",200,null));
        }
        catch (CustomException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ResponseMessage(false,e.getMessage(),400,null));
        }
    }

    @GetMapping()
    public ResponseEntity<ResponseMessage> showContacts(){
        return ResponseEntity.status(200).body(new ResponseMessage(true,"Fetched all contacts successfully",200,contactService.showContacts()));
    }

    @DeleteMapping(path = "{name}")
    public ResponseEntity<ResponseMessage> deleteContact(@PathVariable("name") String name){
        try{
            Contact result = contactService.deleteContact(name);
            return ResponseEntity.status(200).body(new ResponseMessage(true,"Contact deleted successfully",200,result));
        }
        catch (CustomException e){
            return ResponseEntity.status(404).body(new ResponseMessage(false,"Contact not found",404,null));
        }
    }

    @DeleteMapping()
    public ResponseEntity<ResponseMessage> deletePhoneNumberFromContact(@RequestParam("phNo") String phNo){
        if(!validatePhoneNumber(phNo)){
            return ResponseEntity.status(400).body(new ResponseMessage(false,"Invalid phone number",400,null));
        }
        try{
            Contact deletedContact = contactService.deletePhoneNumberFromContact(phNo);
            return ResponseEntity.status(200).body(new ResponseMessage(true,"Number deleted from contact",200,null));
        }
        catch (CustomException e){
            return ResponseEntity.status(404).body(new ResponseMessage(false,e.getMessage(),404,null));
        }
    }

    @PutMapping(path = "{name}")
    public ResponseEntity<ResponseMessage> updateContact(@PathVariable("name") String name,@RequestBody Contact contact) {
//        ResponseMessage responseMessage = validateContactFormat(contact);
//        if(responseMessage != null){
//            return ResponseEntity.status(400).body(responseMessage);
//        }

        if(contact == null){
            return ResponseEntity.status(400).body(new ResponseMessage(false,"Contact details required",400,null));
        }



        try{
            Contact result = contactService.updateContact(name,contact);
            return ResponseEntity.status(202).body(new ResponseMessage(true,"Contact updated successfully",202,result));
        }
        catch (CustomException e){
            return ResponseEntity.status(404).body(new ResponseMessage(false,e.getMessage(),404,null));
        }
    }

    @GetMapping(path="{name}")
    public ResponseEntity<ResponseMessage> getContact(@PathVariable("name") String name){
        try{
           Contact result =  contactService.getContact(name);
           return ResponseEntity.status(200).body(new ResponseMessage(true,"Successfully retrieved the contact",200,result));
        }
        catch (CustomException e){
            return  ResponseEntity.status(404).body(new ResponseMessage(false,e.getMessage(),404,null));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportContacts() throws CustomException, IOException {
        contactService.exportContacts();
        InputStreamResource resource = new InputStreamResource(new FileInputStream("contacts.csv"));
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "contacts.csv");
        // defining the custom Content-Type
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(
                resource,
                headers,
                HttpStatus.OK
        );
    }


    @PostMapping(value="/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> importContacts(@RequestParam MultipartFile file) throws IOException, CustomException {
        try{
            contactService.importContacts(file);
            return ResponseEntity.status(200).body(new ResponseMessage(true,"Contact imported successfully",200,null));
        }catch (CustomException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ResponseMessage(false,e.getMessage(),e.getStatusCode(),null));
        }

    }
}


