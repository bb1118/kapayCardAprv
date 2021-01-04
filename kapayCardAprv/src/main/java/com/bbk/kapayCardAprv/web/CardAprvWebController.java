package com.bbk.kapayCardAprv.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bbk.kapayCardAprv.service.CardAprvService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class CardAprvWebController {
	
	@Resource(name="com.bbk.kapayCardAprv.service.CardAprvService")
	CardAprvService mCardAprvService;
	
	@GetMapping("/")
    public String main() {
        return "main";
    }
	
}
