package com.example.demo;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
	
	@Autowired
    private LoginRepository loginRepository;
	
	
	//ユーザ登録画面表示
    @GetMapping("/register")
    public ModelAndView registerPage(ModelAndView mv) {
        mv.setViewName("register");
        return mv;
    }

	//ユーザ登録
    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        // すでに同じユーザー名が登録されていないか確認
        Login existingUser = loginRepository.findByUsername(username);
        if (existingUser != null) {
            model.addAttribute("error", "このユーザ名はすでに使用されています。");
            return "register";
        }

        Login newUser = new Login();
        newUser.setUsername(username);
        newUser.setPassword(password);
        
        loginRepository.save(newUser);

        // 登録成功したらログイン画面へリダイレクト
        return "redirect:/login";
    }
    
    
	//ログイン画面表示
	@GetMapping(value = "/login")
    public ModelAndView login(ModelAndView mv) {
        mv.setViewName("login"); 
        return mv;
    }
	
	//ログイン処理
	@PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        //入力されたユーザー名で検索
        Login user = loginRepository.findByUsername(username);

        //ユーザーが存在するか、かつパスワードが一致するか確認
        if (user != null && user.getPassword().equals(password)) {
        	session.setAttribute("username", username);
            
            return "redirect:/tasks"; 
            
        } else {
            //ログインできなかった場合
            model.addAttribute("error", "ユーザー名またはパスワードが間違っています。");
            return "login";
        }
    }
	
	// ログアウト処理
    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	
        //セッションを終了
        session.invalidate();
        
        // ログイン画面へリダイレクト
        return "redirect:/login";
    }
    
}
