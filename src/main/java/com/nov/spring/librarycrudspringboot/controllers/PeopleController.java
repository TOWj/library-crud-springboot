package com.nov.spring.librarycrudspringboot.controllers;

import com.nov.spring.librarycrudspringboot.models.Person;
import com.nov.spring.librarycrudspringboot.services.PeopleService;
import com.nov.spring.librarycrudspringboot.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;

    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        model.addAttribute("books", peopleService.getBooksByPersonId(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        //Помещаем в модель нового пустого человека с помощью ModelAttribute
        //В new.html получаем доступ к пустому объекту Person
        return "people/new";
    }


    @PostMapping()//адреса нет, потому что мы переходим без указания страницы в people по post-запросу
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        //Добавляем валидацию для Person по уникальности ФИО (ошибки добавляем в bindingResult)
        personValidator.validate(person, bindingResult);
        //Добавили валидатор для Person, проверяем, есть ли ошибки
        if (bindingResult.hasErrors()) {
            //Если есть, возвращаем снова ту же страницу создания Person
            return "people/new";
        }
        peopleService.save(person);
        //Здесь мы будем создавать нового человека на основе переданных из people/new данных, и добавлять его в БД
        //После добавления, редиректимся в people, т.е. в метод index.
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")//Когда переходим по этому пути
    public String edit(Model model, @PathVariable("id") int id) {//Получаем от get-запроса id и модель
        model.addAttribute("person", peopleService.findOne(id));//кладем в модель по ключу person человека с указанным id
        return "people/edit";//Переходим в edit.html
    }

    @PatchMapping("/{id}")//Переходя по id в people из edit.html с patch-запросом
    public String update(@ModelAttribute("person") @Valid Person person,//Добавили валидатор для Person, проверяем, есть ли ошибки
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }
        //Тащим объект Person и переменную id из адреса
        peopleService.update(id, person);
        return "redirect:/people/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people";
    }


}