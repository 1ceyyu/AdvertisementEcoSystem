package com.example.shopping.controller;

import com.example.shopping.config.WebConfig;
import com.example.shopping.entity.*;
import com.example.shopping.repository.*;
import com.example.shopping.service.AdService; // 引入 AdService
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // 关键：引入事务注解
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired private UserRepository userRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private CartItemRepository cartRepo;
    @Autowired private UserInterestRepository interestRepo;
    @Autowired private AdService adService; // 注入广告服务

    // --- 初始化数据：创建管理员账户 ---
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRole("ADMIN");
                userRepo.save(admin);
            }
        };
    }

    // ================== 用户认证模块 ==================

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String doLogin(String username, String password, HttpSession session, Model model) {
        Optional<User> u = userRepo.findByUsername(username);
        if (u.isPresent() && u.get().getPassword().equals(password)) {
            session.setAttribute("user", u.get());
            // 登录时初始化一个时间戳，防止立即清空
            session.setAttribute("lastResetTime", System.currentTimeMillis());
            return "redirect:/";
        }
        model.addAttribute("error", "账号或密码错误");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register")
    public String doRegister(String username, String password) {
        if (userRepo.findByUsername(username).isPresent()) {
            return "redirect:/register?error";
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole("USER");
        userRepo.save(u);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ================== 商品展示与广告模块 ==================

    @GetMapping("/")
    @Transactional // 必须加事务，用于支持 deleteByUserId 操作
    public String index(HttpSession session, Model model,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) String sort) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);

        // --- 3分钟自动清零兴趣数据逻辑 ---
        long currentTime = System.currentTimeMillis();
        Long lastResetTime = (Long) session.getAttribute("lastResetTime");

        // 如果 Session 中没记录时间，或者距离上次记录超过 3分钟 (180000毫秒)
        if (lastResetTime == null || (currentTime - lastResetTime > 180000)) {
            // 清空该用户的兴趣记录
            interestRepo.deleteByUserId(user.getId());
            // 更新清零时间为现在
            session.setAttribute("lastResetTime", currentTime);
        }

        // --- 1. 获取与筛选商品 ---
        List<Product> products = productRepo.findAll();

        if (category != null && !category.isEmpty())
            products = products.stream().filter(p -> p.getCategory().equals(category)).collect(Collectors.toList());
        if (search != null && !search.isEmpty())
            products = products.stream().filter(p -> p.getName().contains(search)).collect(Collectors.toList());
        if (minPrice != null)
            products = products.stream().filter(p -> p.getPrice() >= minPrice).collect(Collectors.toList());
        if (maxPrice != null)
            products = products.stream().filter(p -> p.getPrice() <= maxPrice).collect(Collectors.toList());

        if ("asc".equals(sort)) {
            products.sort(Comparator.comparing(Product::getPrice));
        } else if ("desc".equals(sort)) {
            products.sort(Comparator.comparing(Product::getPrice).reversed());
        } else if (category == null && search == null) {
            Collections.shuffle(products);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", productRepo.findAll().stream().map(Product::getCategory).distinct().toList());

        // --- 2. 获取用户兴趣并分发广告 ---
        String topInterest = "暂无";
        if ("USER".equals(user.getRole())) {
            List<UserInterest> interests = interestRepo.findByUserIdOrderByClickCountDesc(user.getId());
            if (!interests.isEmpty()) {
                topInterest = interests.get(0).getCategory();
            }
        }
        model.addAttribute("topInterest", topInterest);

        // 调用广告服务 (获取左侧精准，右侧随机)
        Map<String, List<AdDto>> adMap = adService.getAdStrategy(topInterest);
        model.addAttribute("leftAds", adMap.get("left"));
        model.addAttribute("rightAds", adMap.get("right"));

        return "index";
    }

    // ================== 商品详情 ==================

    @GetMapping("/product/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Product p = productRepo.findById(id).orElseThrow();
        model.addAttribute("product", p);

        // 记录用户点击兴趣
        if ("USER".equals(user.getRole())) {
            UserInterest ui = interestRepo.findByUserIdAndCategory(user.getId(), p.getCategory())
                    .orElse(new UserInterest());
            if (ui.getId() == null) {
                ui.setUserId(user.getId());
                ui.setCategory(p.getCategory());
                ui.setClickCount(0);
            }
            ui.setClickCount(ui.getClickCount() + 1);
            interestRepo.save(ui);
        }
        return "detail";
    }

    // ================== 购物车模块 ==================

    @PostMapping("/cart/add")
    public String addCart(Long productId, Integer quantity, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "USER".equals(user.getRole())) {
            CartItem item = cartRepo.findByUserIdAndProductId(user.getId(), productId).orElse(new CartItem());
            if (item.getId() == null) {
                item.setUserId(user.getId());
                item.setProduct(productRepo.findById(productId).get());
                item.setQuantity(0);
            }
            item.setQuantity(item.getQuantity() + quantity);
            cartRepo.save(item);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<CartItem> items = cartRepo.findByUserId(user.getId());
        model.addAttribute("items", items);
        model.addAttribute("total", items.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum());
        return "cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long itemId, @RequestParam Integer change) {
        Optional<CartItem> itemOpt = cartRepo.findById(itemId);
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            int newQuantity = item.getQuantity() + change;
            if (newQuantity <= 0) {
                cartRepo.delete(item);
            } else {
                item.setQuantity(newQuantity);
                cartRepo.save(item);
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeCartItem(@PathVariable Long id) {
        cartRepo.deleteById(id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    @Transactional
    public String checkout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            cartRepo.deleteByUserId(user.getId());
        }
        return "redirect:/cart?checkoutSuccess=true";
    }

    // ================== 管理员模块 ==================

    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/";

        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("interests", interestRepo.findAll());
        return "admin";
    }

    @PostMapping("/admin/product/add")
    public String addProduct(Product product, @RequestParam("imageFile") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String path = System.getProperty("os.name").toLowerCase().startsWith("win")
                        ? WebConfig.LOCAL_PATH : WebConfig.SERVER_PATH;
                File dir = new File(path);
                if (!dir.exists()) dir.mkdirs();

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                file.transferTo(new File(path + fileName));
                product.setImageUrl("/uploads/" + fileName);
            }
            productRepo.save(product);
        } catch (IOException e) { e.printStackTrace(); }
        return "redirect:/admin";
    }

    @GetMapping("/admin/product/delete/{id}")
    @Transactional
    public String deleteProduct(@PathVariable Long id) {
        List<CartItem> items = cartRepo.findByProductId(id);
        cartRepo.deleteAll(items);
        productRepo.deleteById(id);
        return "redirect:/admin";
    }
}