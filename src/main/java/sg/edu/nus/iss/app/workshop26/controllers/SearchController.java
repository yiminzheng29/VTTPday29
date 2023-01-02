package sg.edu.nus.iss.app.workshop26.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.lettuce.core.protocol.DemandAware.Source;
import sg.edu.nus.iss.app.workshop26.models.Comment;
import sg.edu.nus.iss.app.workshop26.models.SuperHero;
import sg.edu.nus.iss.app.workshop26.repositories.MarvelCache;
import sg.edu.nus.iss.app.workshop26.services.MarvelService;

@Controller
@RequestMapping(path="/search")
public class SearchController {
    
    @Autowired
    private MarvelService marvelSvc;

    @Autowired
    private MarvelCache marvelCache;

    @GetMapping
    public String search (@RequestParam String heroName, Model model) {
        List<SuperHero> results = null;

        Optional<List<SuperHero>> opt = marvelCache.get(heroName);
        if (opt.isEmpty()) {
            results = marvelSvc.search(heroName);
            marvelCache.cache(heroName, results);
        } else {
            results=opt.get();
            System.out.printf(">>>> from CACHE\n");
        }
        model.addAttribute("results", results);

        List<Comment> comments = marvelCache.search(heroName);
        System.out.println(">>>> Comments: " + comments.size());

        model.addAttribute("hasComments", comments.size()>0);
        model.addAttribute("comments", comments);

        return "result";
    }

    @PostMapping
    public String postComment(@RequestBody MultiValueMap<String, String> form, Model model) {
        String id = form.getFirst("id");
        String user = form.getFirst("user");
        String text = form.getFirst("text");

        Comment c = new Comment();
        c.setCharacterId(id);
        c.setUser(user);
        c.setText(text);
        c.setTimestamp(LocalDateTime.now().toString());

        model.addAttribute("review", c);
        marvelCache.insertComment(c);

        return "comment";
    }
}
