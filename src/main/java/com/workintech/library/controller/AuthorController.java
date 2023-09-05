package com.workintech.library.controller;

import com.workintech.library.entity.Author;
import com.workintech.library.entity.Book;
import com.workintech.library.entity.Category;
import com.workintech.library.mapping.AuthorResponse;
import com.workintech.library.mapping.BookResponse;
import com.workintech.library.service.AuthorService;
import com.workintech.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController {
    private AuthorService authorService;
    private BookService bookService;

    @Autowired
    public AuthorController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("/")
    public List<AuthorResponse> get() {
        List<Author> authors = authorService.findAll();
        List<AuthorResponse> authorResponses = new ArrayList<>();
        for(Author author: authors){
            List<BookResponse> bookResponses = new ArrayList<>();
            for(Book book: author.getBooks()){
                bookResponses.add(new BookResponse(book.getId(), book.getName(), book.getCategory().getName()));
            }
            authorResponses.add(new AuthorResponse(author.getId(), author.getFirstName(), author.getLastName(),
                    bookResponses));
        }
        return authorResponses;
    }
    @GetMapping("/{id}")
    public AuthorResponse getById(@PathVariable int id) {
        Author author = authorService.findById(id);
        Book book = bookService.findById(id);
        List<BookResponse> bookResponses = new ArrayList<>();
        bookResponses.add(new BookResponse(book.getId(), book.getName(), book.getCategory().getName()));
        return new AuthorResponse(author.getId(), author.getFirstName(), author.getLastName(), bookResponses);
    }
    @PostMapping("/")
    public AuthorResponse save(@RequestBody Author author){
        Author savedAuthor = authorService.save(author);
        return new AuthorResponse(savedAuthor.getId(), savedAuthor.getFirstName(),
                savedAuthor.getLastName(), null);
    }

    @PostMapping("/{bookId}")
    public AuthorResponse save(@RequestBody Author author, @PathVariable int bookId){
        Book book = bookService.findById(bookId);
        author.addBook(book);
        book.setAuthor(author);
        authorService.save(author);
        return new AuthorResponse(author.getId(), author.getFirstName(),
                author.getLastName(), null);
    }

    @PutMapping("/{authorId}")
    public AuthorResponse update(@RequestBody Author author, @PathVariable int authorId){
        Author foundAuthor = authorService.findById(authorId);
        if (foundAuthor != null){
            author.setFirstName(foundAuthor.getFirstName());
            author.setLastName(foundAuthor.getLastName());
            author.setId(authorId);
            authorService.save(author);
            Book book = bookService.findById(authorId);
            List<BookResponse> bookResponses = new ArrayList<>();
            bookResponses.add(new BookResponse(book.getId(), book.getName(), book.getCategory().getName()));
            return new AuthorResponse(author.getId(), author.getFirstName(), author.getLastName(), bookResponses);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public AuthorResponse delete(@PathVariable int id) {
        Author author = authorService.findById(id);
        if (author != null) {
            authorService.delete(author);
            return new AuthorResponse(author.getId(), author.getFirstName(), author.getLastName(), null);
        }
        return null;
    }
}
