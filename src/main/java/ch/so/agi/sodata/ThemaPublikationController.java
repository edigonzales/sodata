package ch.so.agi.sodata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/themapublikation")
    public ModelAndView showAll() {
        ModelAndView mav = new ModelAndView("themapublikation");
        mav.addObject("themaPublikationList", new ArrayList<ThemaPublikation>());
        return mav;
    }

    @GetMapping("/themapublikation/{id}")
    public ModelAndView showDetail(@PathVariable(name = "id") String id) {
        ThemaPublikation themaPublikation = themaPublikationService.findById(id)
                .orElseThrow(() -> new IllegalStateException("Item not found with id: " + id));
        ModelAndView mav = new ModelAndView("detail");
        mav.addObject("themaPublikation", themaPublikation);
        return mav;
    }

    
}
