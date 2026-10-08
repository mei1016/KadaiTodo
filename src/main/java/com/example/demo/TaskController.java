package com.example.demo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TaskController {

	@Autowired
	private TasksRepository tasksRepository;
	
	//タスク一覧画面表示
	@GetMapping(value = "/tasks")
    public ModelAndView tasks(@RequestParam(name = "page", defaultValue = "1") int page,
            HttpSession session,
            ModelAndView mv) {
		
		String username = (String) session.getAttribute("username");
		
		if (username == null) {
            return new ModelAndView("redirect:/login");
        }
		
        mv.setViewName("tasks/list"); 
        
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        
        Page<Tasks> taskPage = tasksRepository.findByUsername(username, pageable);
        
        mv.addObject("tasks", taskPage.getContent()); 
        mv.addObject("taskPage", taskPage);
        
        return mv;
    }
	
	//タスク新登録画面表示
	@GetMapping(value = "/tasks/new")
    public ModelAndView newtasks(ModelAndView mv) {
        mv.setViewName("tasks/form-new"); 
        
        return mv;
    }
	
	//タスク登録
	@PostMapping("/tasks")
	public String register(
	        HttpSession session,
	        @RequestParam("title") String title,
	        @RequestParam("content") String content,
	        @RequestParam("name") String name,
	        @RequestParam("startDate") String startDate,
	        @RequestParam("endDate") String endDate,
	        Model model) {
	    
	    String username = (String) session.getAttribute("username");
	    if (username == null) {
	        return "redirect:/login";
	    }
	    
	    //新しいタスクの箱を作ってデータを入れる
	    Tasks newTasks = new Tasks();
	    newTasks.setUsername(username);
	    newTasks.setTitle(title);
	    newTasks.setContent(content);
	    newTasks.setName(name);
	    
	    //String型の日付をLocalDateに変換
	    if (startDate != null && !startDate.isEmpty()) {
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate date = LocalDate.parse(startDate, formatter);
	        newTasks.setStartDate(date);
	    }
	    if (endDate != null && !endDate.isEmpty()) {
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate date = LocalDate.parse(endDate, formatter);
	        newTasks.setEndDate(date);
	    }
	    
	    tasksRepository.save(newTasks);
	    
	    return "redirect:/tasks";
	}
	
	// 編集画面表示
    @GetMapping("/tasks/edit/{id}")
    public ModelAndView editTask(@PathVariable("id") Long id,
            HttpSession session,
            ModelAndView mv) {
        
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return new ModelAndView("redirect:/login");
        }
        
        //指定されたIDのタスクデータを取得
        Tasks task = tasksRepository.findById(id).orElse(null);
        
        mv.setViewName("tasks/form-edit"); 
        
        //タスクのデータを渡す
        mv.addObject("task", task);
        
        return mv;
    }
    
    //アップデート処理
    @PostMapping("/tasks/update/{id}")
    public String updateTask(
            @PathVariable("id") Long id,
            @ModelAttribute Tasks task,
            HttpSession session) {
        
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        
        //画面から送られてきたデータに、念のためURLのIDとユーザー名をセットする
        task.setId(id);
        task.setUsername(username);
        
        tasksRepository.save(task);
        
        //一覧画面にリダイレクト
        return "redirect:/tasks";
    }
    
    //削除処理
    @PostMapping("/tasks/delete/{id}")
    public String deleteTask(
            @PathVariable("id") Long id,
            HttpSession session) {
      
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        
        //指定されたIDのタスクデータを取得
        Tasks task = tasksRepository.findById(id).orElse(null);
        
        //タスクが存在する、かつ今ログインしている人のタスクの時だけ削除
        if (task != null && task.getUsername().equals(username)) {
            tasksRepository.delete(task);
        }
        
        //一覧画面にリダイレクト
        return "redirect:/tasks";
    }
}