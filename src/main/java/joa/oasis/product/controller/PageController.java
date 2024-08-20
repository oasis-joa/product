package joa.oasis.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/start")
    public String startPage() {
        return "start";
    }
}