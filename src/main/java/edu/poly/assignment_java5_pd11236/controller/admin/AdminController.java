package edu.poly.assignment_java5_pd11236.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import edu.poly.assignment_java5_pd11236.model.Users;
import edu.poly.assignment_java5_pd11236.service.HoaDonService;
import edu.poly.assignment_java5_pd11236.service.LoaiService;
import edu.poly.assignment_java5_pd11236.service.SanPhamService;
import edu.poly.assignment_java5_pd11236.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoaiService loaiService;

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HttpSession session;

    @GetMapping
    public String home(Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (isUnauthorized(currentUser)) {
            return "redirect:/";
        }
        
        model.addAttribute("totalUsers", userService.getAllUsers(0, 1000).getTotalElements());
        model.addAttribute("totalCategories", loaiService.getAllLoai(0, 1000).getTotalElements());
        model.addAttribute("totalProducts", sanPhamService.getAllSanPham(0, 1000).getTotalElements());
        model.addAttribute("totalOrders", hoaDonService.countReceivedOrders());
        return "admin/user/userManager";        
    }

    @GetMapping("/user")
    public String userManager(Model model, @RequestParam(defaultValue = "0") int page) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (isUnauthorized(currentUser)) {
            return "redirect:/";
        }

        Page<Users> userPage = userService.getAllUsers(page, 8);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        return "admin/user/userManager";
    }

    @GetMapping("/user/create")
    public String userCreate(Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (isUnauthorized(currentUser)) {
            return "redirect:/";
        }
        model.addAttribute("user", new Users());
        return "admin/user/createUser";
    }

    @PostMapping("/user/create")
    public String userInsert(Model model, @ModelAttribute("user") Users user) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (isUnauthorized(currentUser)) {
            return "redirect:/";
        }
        
        try {
            userService.register(user);
            model.addAttribute("successMessage", "Tạo tài khoản thành công");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "admin/user/createUser";
    }

    @GetMapping("/user/edit/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (isUnauthorized(currentUser)) {
            return "redirect:/";
        }
        
        try {
            Users user = userService.getUserById(id);
            model.addAttribute("user", user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "admin/user/updateUser";
    }

    @PostMapping("/user/update/{id}")
    public String updateUser(Model model, @PathVariable("id") String id, @ModelAttribute("user") Users updatedUser) {
        Users currentUser = (Users) session.getAttribute("currentUser");
        if (isUnauthorized(currentUser)) {
            return "redirect:/";
        }
        
        try {
            userService.updateUser(id, updatedUser);
            model.addAttribute("successMessage", "Cập nhật tài khoản thành công");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "admin/user/updateUser";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tài khoản thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/user";
    }

    private boolean isUnauthorized(Users user) {
        return user == null || !user.isVaitro();
    }
}
