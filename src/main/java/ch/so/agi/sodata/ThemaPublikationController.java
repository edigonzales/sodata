package ch.so.agi.sodata;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
//@RequestMapping("/api/themapublikation")
public class ThemaPublikationController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ThemaPublikationService themaPublikationService;
    
    public ThemaPublikationController(ThemaPublikationService themaPublikationService) {
        this.themaPublikationService = themaPublikationService;
    }

    //@GetMapping("/api/themapublikation")
    @GetMapping(path = "/api/themapublikation", produces=MediaType.APPLICATION_JSON_VALUE)
    public List<ThemaPublikation> findAll() {
        return themaPublikationService.findAll();
    }
    
    @GetMapping("/web/themapublikation")
    public String showAll(Model model) {
        System.out.println(themaPublikationService.findAll());
        model.addAttribute("themaPublikationList", themaPublikationService.findAll());
        return "themapublikation";
    }
    
}
