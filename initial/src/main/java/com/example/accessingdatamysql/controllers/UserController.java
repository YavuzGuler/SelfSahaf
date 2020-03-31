package com.example.accessingdatamysql.controllers;

import com.example.accessingdatamysql.dao.AddressRepository;
import com.example.accessingdatamysql.dao.PostalRepository;
import com.example.accessingdatamysql.dao.UserRepository;
import com.example.accessingdatamysql.models.Address;
import com.example.accessingdatamysql.models.CardInfo;
import com.example.accessingdatamysql.models.PostalCode;
import com.example.accessingdatamysql.models.User;
import com.google.common.hash.Hashing;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping(path="/user")
@Api(value = "user", description = "All about users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostalRepository postalRepository;

    @Autowired
    private AddressRepository addressRepository;

    @ApiOperation("Don't post userID, address and card here. Use update for that")
    @PostMapping(path="/add")
    public @ResponseBody
    String addNewUser (@RequestBody User user) {


        User user1 = userRepository.findUserByEmail(user.getEmail());


        if(user1!= null)
            return "This mail is already in use";

        String passSha;
        String pass;
        if(user.getPassword() != null)
            pass = user.getPassword();
        else return "pass";

        passSha = Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString();
        user.setPassword(passSha);


        userRepository.save(user);
        return "Saved";
    }

    @ApiOperation("You can update any user variable, if mail is changed; give old mail as param")
    @PostMapping(path= "/update")
    public @ResponseBody String updateUser (@RequestBody User user) {
        User user1= userRepository.findUserByUserID(user.getUserID());

        if(user1 == null)
            return "Requested User not found";
        if(user.getAddresses() != null) {
            for (Address a : user.getAddresses()) {
                PostalCode postalCode = postalRepository.findByPostalCode(a.getPostalCode().getPostalCode());
                if (postalCode != null) {
                    a.setPostalCode(postalCode);
                }
                user1.getAddresses().add(a);
            }
        }
        Set<CardInfo> cards = new HashSet<>();
        if(user.getCards() != null) {
            for (CardInfo c : user.getCards()) {

                Set<User> s = new HashSet<User>();
                s.add(user1);
                c.setUsers(s);
                cards.add(c);
            }
        }
        if(user.getPassword() != null)
        {
            String passSha = Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString();
            user1.setPassword(passSha);
        }
        if(!cards.isEmpty())
            user1.setCards(cards);
        userRepository.save(user1);
        return "Saved";
    }

    @ApiOperation("Get user with id")
    @GetMapping(path="/get")
    public @ResponseBody User getUser(@RequestParam Integer userID)
    {
        return userRepository.findUserByUserID(userID);
    }

    @ApiOperation("Get all users")
    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    @PostMapping(path="/login")
    public @ResponseBody String login(@RequestParam String email, @RequestParam String password) {
        User user;
        user = userRepository.findUserByEmail(email);
        if(user == null){
            return "There is no account with email ".concat(email);
        }
        if(user.getPassword().equals(Hashing.sha256().hashString(password,StandardCharsets.UTF_8).toString())){
            return "Logged In";
        }
        else {
            return "Incorrect Password";
        }
    }

    @ApiOperation("Choose seller address from users addresses. Give the pre-saved addressName")
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
    @ApiOperation("w/ id for update")
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
}
