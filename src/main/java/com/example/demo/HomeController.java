package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageRepository messageRepository;
    AuthorRepository authorRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @RequestMapping("/image")
    public String listActors(Model model) {
        model.addAttribute("authors", authorRepository.findAll());
        return "authorlist";
    }

    @GetMapping("/add")
    public String messageform(Model model) {
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @GetMapping("/image")
    public String newAuthor(Model model) {
        model.addAttribute("author", new Author());
        return "form";
    }

    @PostMapping("/process")
    public String processAuthor(@Valid@ModelAttribute Message message,
                                BindingResult result,
                                @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()){
            return "messageform";
        }
        else if (file.isEmpty()) {
            return "messageform";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setImage(uploadResult.get("url").toString());
            messageRepository.save(message);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";
    }

//    @PostMapping("/process")
//    public String processForm(@Valid Message message,
//                              BindingResult result){
//        if (result.hasErrors()){
//            return "messageform";
//        }
//        messageRepository.save(message);
//        return "redirect:/";
//    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }
    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }
}
