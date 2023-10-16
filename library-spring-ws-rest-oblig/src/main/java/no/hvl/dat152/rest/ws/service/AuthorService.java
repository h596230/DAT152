/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
	
	
	public Author saveAuthor(Author author) throws NullPointerException {
		if(author != null) {
			return authorRepository.save(author);
		}
		throw new NullPointerException("Author is empty");
	}
	public void delteAuthorById(long id) {
		authorRepository.deleteById(id);
	}

	public List<Author> getAuthors(){
		return (List<Author>) authorRepository.findAll();
	}
	public Author findById(long id) throws AuthorNotFoundException {
		
		return authorRepository.findById(id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));
	}

}
