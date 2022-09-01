package com.nov.spring.librarycrudspringboot.services;


import com.nov.spring.librarycrudspringboot.models.Book;
import com.nov.spring.librarycrudspringboot.models.Person;
import com.nov.spring.librarycrudspringboot.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }


    public List<Book> findAll(boolean SortByYear) {
        if (SortByYear) {
            return booksRepository.findAll(Sort.by("year"));
        } else {
            return booksRepository.findAll();
        }
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean SortByYear) {
        if (SortByYear) {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        } else {
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
        }
    }

    public Book findOne(int id) {
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }

    public List<Book> searchByTitle(String query) {
        return booksRepository.findByTitleStartingWith(query);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book book) {
        Book bookToUpdate = booksRepository.findById(id).get();//кидаем в Persistence контекст

        book.setId(id);
        //В сущности, которая приходит из вью, владелец - null, поэтому назначаем owner'а
        book.setOwner(bookToUpdate.getOwner()); // чтобы не терять связь при обновлении книги

        booksRepository.save(book);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    public Person getBookOwner(int id) {
        //возвращает null, если владельца нет
        return booksRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    @Transactional(readOnly = false)
    public void release(int id) {
        booksRepository.findById(id).ifPresent(//сеттеры вызываются на сущности в Persistence контексте
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);//обнулим время взятия
                });
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {
        booksRepository.findById(id).ifPresent(//если книга существует
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date());//настоящее время
                }
        );
    }
}
