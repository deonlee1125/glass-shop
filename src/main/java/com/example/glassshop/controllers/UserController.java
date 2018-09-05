package com.example.glassshop.controllers;

import com.example.glassshop.models.User;
import com.example.glassshop.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;


@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("title", "Registration");
        User user = new User();
        model.addAttribute("user", user);

        return "user/register";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(@ModelAttribute @Valid User user, Errors errors, Model model) {

        List<User> sameName = userDao.findByUsername(user.getUsername());

        if (!errors.hasErrors() && sameName.isEmpty() && user.getPassword().equals(user.getPasswordVerify())) {
            model.addAttribute("user", user);
            userDao.save(user);
            return "user/index";

        } else {
            model.addAttribute("title", "Please Try Again");
            return "user/register";
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String displayLoginForm(Model model, String logout) {
        model.addAttribute("title", "Login Page");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        model.addAttribute(new User());
        return "user/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String processLoginForm(Model model, @ModelAttribute @Valid User user, HttpServletResponse response) {
        List<User> u = userDao.findByUsername(user.getUsername());
        if (u.isEmpty()) {
            model.addAttribute("message", "Username already exists. Please choose another.");
            model.addAttribute("title", "Login Page");
            return "user/login";
        }

        User loggedIn = u.get(0);
        if (loggedIn.getPassword().equals(user.getPassword())) {

            Cookie c = new Cookie("user", user.getUsername());
            c.setPath("/");
            response.addCookie(c);
            return "redirect:/art";

        } else {
            model.addAttribute("title", "Login Page");
            //User user = new User();
            model.addAttribute("message", "Invalid Password");
            return "user/login";
        }
    }

    @RequestMapping(value = {"logout"}, method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                c.setMaxAge(0);
                c.setPath("/");
                response.addCookie(c);
            }
        }
        return "user/login";
    }
}
/*
        *****************
        *         ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 6 || user.getUsername().length() > 15) {
            errors.rejectValue("username", "Username must be between 5 and 15 characters long.");
        }
        if (userDao.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Username taken. please select another.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 15) {
            errors.rejectValue("password", "Password must be between 8 and 15 characters long.");
        }

        if (!user.getPasswordVerify().equals(user.getPassword())) {
            errors.rejectValue("passwordVerify", "Passwords must match");
        }
        ******************
        *
        *         } else {
            //model.addAttribute("user", user);
            model.addAttribute("title", "Please Try Again");
            //model.addAttribute(errors);
            //model.addAttribute("errors" "errors")
            //if (!sameName.isEmpty()) {
                //model.addAttribute(errors);
                //model.addAttribute("message", "Username is taken. Please provide another.");
                //user.setUsername("");
            //}
            //if (errors.hasErrors()) {
                //model.addAttribute(errors);
                //model.addAttribute("message", "Password must be 8-15 characters.");
                //user.setPassword("");
                //user.setPasswordVerify("");
            }

            //if (!user.getPassword().equals(user.getPasswordVerify())) {
                //model.addAttribute("message", "Passwords must match.");
                //user.setPassword("");
                //user.setPasswordVerify("");
            //}
        //}
        */

