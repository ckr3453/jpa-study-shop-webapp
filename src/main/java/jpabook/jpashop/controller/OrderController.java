package jpabook.jpashop.controller;

import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.entity.Member;
import jpabook.jpashop.domain.entity.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/orders")
    public String list(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){

        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        model.addAttribute("orderSearch", orderSearch);
        return "order/orderList";
    }

    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItem();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        // 이런식으로 서비스단으로 다 넘기고 컨트롤러에서 엔티티 호출은 가급적 자제 (읽기는 괜찮)
        // 컨트롤러에서는 식별자만 넘기기
        // 왜냐면 컨트롤러에선 트랜잭션 없이 넘기기 때문에 영속성 컨텍스트와 엮지못하고 꼬여버림
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
