package com.example.accessingdatamysql.controllers;

import com.example.accessingdatamysql.models.Address;
import com.example.accessingdatamysql.models.User;
import com.example.accessingdatamysql.dao.AddressRepository;
import com.example.accessingdatamysql.dao.PostalRepository;
import com.example.accessingdatamysql.dao.UserRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;

@Controller // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
@Api(value = "asdas", description = "Controller")
public class MainController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PostalRepository postalRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUser (@RequestBody User user) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        /*User n = new User();
        n.setName(name);
        n.setEmail(email);*/

        User user1 = userRepository.findUserByEmail(user.getEmail());

        if(user1!= null)
            return "This mail is already in use";
        userRepository.save(user);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {

        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
    @GetMapping(path="/alladdress")
    public @ResponseBody Iterable<Address> getAllAddress() {
        // This returns a JSON or XML with the users
        return addressRepository.findAll();
    }
    @PostMapping(path="/login")
    public @ResponseBody String login(@RequestParam String email, @RequestParam String password) {
        User user;

        user = userRepository.findUserByEmail(email);
        if(user == null){
            return "Incorrect email";
        }
        if(user.getPassword() == password){
            return "Logged In";
        }
        else {
            return "Incorrect Password";
        }
    }
    @PostMapping(path= "/addSellerAddress")
    public @ResponseBody String addSellerAddress(@RequestParam Integer userID, @RequestParam String addressName){
            User user = userRepository.findUserByUserID(userID);
            if(user == null){
                return "Requested user not found";
            }

            Address address = new Address();
            //Address[] addresses = user.getAddresses();
            boolean found = false;
            for(Address a:user.getAddresses()){
                if(addressName.equals(a.getAddressName()))
                {
                    address.setAddressID(a.getAddressID());
                    found = true;
                }
            }
            if(!found)
                return "address not found";

            user.setSellerAddressID(address);

            userRepository.save(user);
            return "saved";
    }
    @PostMapping(path= "/addAddress")
    public @ResponseBody String addAddress(@RequestParam Integer userID, @RequestBody Address address){
        User user = userRepository.findUserByUserID(userID);
        if(user == null){
            return "Requested user not found";
        }

        addressRepository.save(address);
        user.getAddresses().add(address);
        userRepository.save(user);
        return "saved";
    }
    @PostMapping(path ="/addBook")
    public @ResponseBody String addBook(@RequestParam Integer sellerID )
    {
        return "Not Implemented";
    }
    /*
    @PostMapping(path="/addpostal")
    public @ResponseBody String addPostal(@RequestBody PostalCode postalCodeCity){
        postalRepository.save(postalCodeCity);
        return "saved";
    }*/
}