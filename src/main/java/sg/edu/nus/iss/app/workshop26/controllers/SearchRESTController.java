package sg.edu.nus.iss.app.workshop26.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import sg.edu.nus.iss.app.workshop26.models.SuperHero;
import sg.edu.nus.iss.app.workshop26.repositories.MarvelCache;
import sg.edu.nus.iss.app.workshop26.services.MarvelService;

@RestController
@RequestMapping(path="/api/character", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchRESTController {

    @Autowired
    private MarvelService marvelSvc;

    @Autowired
    private MarvelCache marvelCache;

    @GetMapping("{heroName}")
    public ResponseEntity<String> search (@PathVariable String heroName) {
        List<SuperHero> results = null;

        Optional<List<SuperHero>> opt = marvelCache.get(heroName);
        if (opt.isEmpty()) {
            results = marvelSvc.search(heroName);
            marvelCache.cache(heroName, results);
        } else {
            results=opt.get();
            System.out.printf(">>>> from CACHE\n");
        }
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(results.toString());
        
    }
}
