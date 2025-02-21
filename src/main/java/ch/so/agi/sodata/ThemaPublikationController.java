package ch.so.agi.sodata;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
//@RequestMapping("/api/themapublikation")
public class ThemaPublikationController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ThemaPublikationService themaPublikationService;
    
    public ThemaPublikationController(ThemaPublikationService themaPublikationService) {
        this.themaPublikationService = themaPublikationService;
    }

    @GetMapping(path = "/api/themapublikation", produces=MediaType.APPLICATION_JSON_VALUE)
    public List<ThemaPublikation> findAll() {
        return themaPublikationService.findAll();
    }
    
//    @PostMapping(path = "/search") 
//    public List<ThemaPublikation> search(@RequestParam(name = "search") String search) {
//        return themaPublikationService.findByFilter(search);
//    }

    @PostMapping(path = "/search") 
    public ModelAndView search(@RequestParam(name = "search") String search) {
        ModelAndView mav = new ModelAndView("search-results");
        if (search == null || search.length() < 3) {
            mav.addObject("themaPublikationList", themaPublikationService.findAll());            
        } else {
            mav.addObject("themaPublikationList", themaPublikationService.findByFilter(search));            
        }
        return mav;
    }

    @GetMapping("/web/themapublikation")
    public ModelAndView showAll() {
        ModelAndView mav = new ModelAndView("themapublikation");
        mav.addObject("themaPublikationList", new ArrayList<ThemaPublikation>());
        return mav;
    }
    
}
