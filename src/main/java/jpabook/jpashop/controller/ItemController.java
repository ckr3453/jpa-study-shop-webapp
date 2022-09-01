package jpabook.jpashop.controller;

import jpabook.jpashop.domain.form.BookForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItem();
        model.addAttribute("items", items);
        return "items/itemList";

    }

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("bookForm", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(@Valid BookForm form, BindingResult result){
        if(result.hasErrors()){
            return "items/createItemForm";
        }
        Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());
        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book book = (Book) itemService.findOne(itemId);

        BookForm bookForm = new BookForm();
        bookForm.setId(book.getId());
        bookForm.setName(book.getName());
        bookForm.setPrice(book.getPrice());
        bookForm.setAuthor(book.getAuthor());
        bookForm.setIsbn(book.getIsbn());

        model.addAttribute("bookForm", bookForm);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @Valid BookForm bookForm, BindingResult result){
        if(result.hasErrors()){
            return "items/updateItemForm";
        }

        if(itemService.findOne(bookForm.getId()) == null){
            return "items";
        }

        // 준영속 엔티티 = 영속성 컨텍스트가 더는 관리하지 않는 엔티티
        // 기존 식별자를 가지고 있으면 임의로 만들어낸 엔티티라도 준영속 엔티티라고 볼수 있다.
        // 준영속 엔티티를 대상으로 수정하는 방법은 2가지로 "변경감지기능(dirty checking)" 사용 혹은 "병합(merge)" 사용이다.
        // 이 방식은 병합(merge) 이다. (권장하지않음)
//        Book book = Book.createBook(bookForm.getId(), bookForm.getName(), bookForm.getPrice(), bookForm.getStockQuantity(), bookForm.getAuthor(), bookForm.getIsbn());
//        itemService.saveItem(book);

        // 변경감지기능 (dirty checking) 활용
        itemService.updateItem(itemId, bookForm.getName(), bookForm.getPrice(), bookForm.getStockQuantity());
        return "redirect:/items";
    }
}
