package com.dangdang.config.face.controller;

import com.dangdang.config.face.service.INodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @Autowired
    private INodeService nodeService;

    @GetMapping(value = "/login")
    public ModelAndView login() {
        return new ModelAndView("login", "logins", nodeService.findLogins());
    }

}
