/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;


import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.service.AuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @GetMapping("/authors")
    public ResponseEntity<Object> getAllAuthors(){
        List<Author> authors = authorService.findAll();
        if(authors.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable("id") long id) throws Exception {
        Author author = authorService.findById(id);
        if(author == null){
            throw new Exception("Author with that id not found");
        }
        return new ResponseEntity<>(author,HttpStatus.OK);
    }
    @PutMapping("/authors/{id}")
    public ResponseEntity<Object> updateAuthor(@PathVariable("id") long id,@RequestBody Author author) throws Exception {

        if(author == null){
            throw new Exception("Author with that id not found");
        }
        authorService.updateAuthor(id,author);
        List<Author> list = authorService.findAll();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @PutMapping("/authors")
    public ResponseEntity<Object> addAuthor(@RequestBody Author author){
        authorService.saveAuthor(author);
        List<Author> authors = authorService.findAll();
        return new ResponseEntity<>(authors,HttpStatus.OK);
    }
}
