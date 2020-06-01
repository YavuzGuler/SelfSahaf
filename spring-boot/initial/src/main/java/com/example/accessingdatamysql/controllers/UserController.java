package com.example.accessingdatamysql.controllers;

import com.example.accessingdatamysql.auth.UserDetailsImp;
import com.example.accessingdatamysql.dao.AddressRepository;
import com.example.accessingdatamysql.dao.PostalRepository;
import com.example.accessingdatamysql.dao.UserRepository;
import com.example.accessingdatamysql.models.*;
import com.example.accessingdatamysql.security.UserRole;
import com.google.common.hash.Hashing;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.example.accessingdatamysql.security.UserRole.*;

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

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @ApiOperation("Don't post userID, address and card here. Use update for that")
    @PostMapping(path="/add")
    public @ResponseBody String addNewUser (@RequestBody User user, HttpServletResponse response) {

        Optional<User> findUser = userRepository.findUserByEmail(user.getEmail());


        if(findUser.isPresent()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "This mail is already in use";
        }

        /*if (user.getRole().equals("ROLE_SELLER")) user.setRole(SELLER.name());
        else if (user.getRole().equals("ROLE_ADMIN")) user.setRole(ADMIN.name());
        else user.setRole(USER.name());*/

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "Saved";
    }

    @ApiOperation("You can update any user variable, if mail is changed; give old mail as param")
    @PostMapping(path= "/update")
    public @ResponseBody String updateUser (@RequestBody User user, HttpServletResponse response) {


        Optional<User> findUser = userRepository.findUserByUserID(user.getUserID());

        if(findUser.isPresent()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return "Requested User not found";
        }

        if(user.getAddresses() != null) {
            for (Address a : user.getAddresses()) {
                PostalCode postalCode = postalRepository.findByPostalCode(a.getPostalCode().getPostalCode());
                if (postalCode != null) {
                    a.setPostalCode(postalCode);
                }
                findUser.get().getAddresses().add(a);
            }
        }

        Set<CardInfo> cards = new HashSet<>();
        if(user.getCards() != null) {
            for (CardInfo c : user.getCards()) {
                Set<User> s = new HashSet<User>();
                s.add(findUser.get());
                c.setUsers(s);
                cards.add(c);
            }
        }

        if(user.getPassword() != null)
            findUser
                    .get()
                    .setPassword(
                            passwordEncoder.encode(user.getPassword())
                    );


        if(!cards.isEmpty()) findUser.get().setCards(cards);

        userRepository.save(findUser.get());
        return "Saved";

    }

    @ApiOperation("Get user with id")
    @GetMapping(path="/get")
    public @ResponseBody Optional<User> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findUserByUserID(((UserDetailsImp) auth.getPrincipal()).getUserID());
    }

    @ApiOperation("Get all users")
    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }


    @ApiOperation("Choose seller address from users addresses. Give the pre-saved addressName")
    @PostMapping(path= "/addSellerAddress")
    public @ResponseBody String addSellerAddress(@RequestBody Address address,HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findUserByUserID(((UserDetailsImp) auth.getPrincipal()).getUserID());
        if(!user.isPresent()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Requested user not found";
        }
        PostalCode postalCode = postalRepository.findByPostalCode(address.getPostalCode().getPostalCode());
        if (postalCode != null) {
            address.setPostalCode(postalCode);


            addressRepository.save(address);

            userRepository.save(user.get());
        }
        return "saved";

    }

    @ApiOperation("w/ id for update")
    @PostMapping(path= "/addAddress")
    public @ResponseBody String addAddress(@RequestBody Address address, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findUserByUserID(((UserDetailsImp) auth.getPrincipal()).getUserID());

        if(!user.isPresent()){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Requested user not found";
        }
        if(user.get().getAddresses() != null) {
                PostalCode postalCode = postalRepository.findByPostalCode(address.getPostalCode().getPostalCode());
                if (postalCode != null) {
                    address.setPostalCode(postalCode);
                }
                user.get().getAddresses().add(address);
        }
        addressRepository.save(address);
        user.get().getAddresses().add(address);
        userRepository.save(user.get());

        return "Saved";

    }
    @ApiOperation("Set Address")
    @PostMapping(path = "/updateAddress")
    public @ResponseBody String updateAddress(@RequestBody Address address, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "Not implemented";
    }


    @ApiOperation("Get Addresses")
    @GetMapping(path = "/getadress")
    public @ResponseBody
    Iterable<Address> getAddress()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findUserByUserID(((UserDetailsImp) auth.getPrincipal()).getUserID());
        return user.get().getAddresses();

    }
    @ApiOperation("Get SellerAdress")
    @GetMapping(path = "/getselleradress")
    public @ResponseBody
    Address getSellerAddress()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findUserByUserID(((UserDetailsImp) auth.getPrincipal()).getUserID());
        return user.get().getSellerAddressID();
    }

    @ApiOperation("Get Sellings")
    @GetMapping(path = "/getsells")
    public @ResponseBody
    Set<Sells> getSells()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findUserByUserID(((UserDetailsImp) auth.getPrincipal()).getUserID());
        return user.get().getSells();
    }
}
