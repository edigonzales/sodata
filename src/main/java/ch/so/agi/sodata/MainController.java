package ch.so.agi.sodata;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        //return "index";
        return "redirect:/themapublikation";
    }    
}
