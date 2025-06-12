package com.example.jdshoes.controller.admin;

import com.example.jdshoes.entity.Color;
import com.example.jdshoes.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class ColorController {

    @Autowired
    private ColorService colorService;

    @GetMapping("/color-all")
    public String listColor(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "sort", defaultValue = "name,asc") String sortField) {
        int pageSize = 8;
        String[] sortParams = sortField.split(",");
        String sortFieldName = sortParams[0];
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(sortDirection, sortFieldName);

        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<Color> colorPage = colorService.findAll(pageable);

        model.addAttribute("sortField", sortFieldName);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("items", colorPage);

        return "admin/color";
    }

    @GetMapping("/color-create")
    public String viewAddColor(Model model){
        Color color = new Color();
        model.addAttribute("action", "/admin/color-save");
        model.addAttribute("Color", color);
        return "admin/color-create";
    }

    @PostMapping("/color-save")
    public String addColor(RedirectAttributes redirectAttributes, @Validated @ModelAttribute("Color") Color color) {
        try {
            colorService.createColor(color);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm màu mới thành công");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/color-create";
        }
        return "redirect:/admin/color-all";
    }

    @PostMapping("/color-update/{id}")
    public String update(@PathVariable("id") Long id,
                         @Validated @ModelAttribute("Color") Color color, RedirectAttributes redirectAttributes) {
        if (colorService.existsById(id)) {
            try {
                Color color1 = colorService.updateColor(color);
                redirectAttributes.addFlashAttribute("successMessage", "Mã màu " + color1.getCode() + " cập nhật thành công");

            }catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/admin/edit-color/" + id;
            }
            return "redirect:/admin/color-all";
        } else {
            return "404";
        }
    }

    @GetMapping("/edit-color/{id}")
    public String viewEdit(@PathVariable("id") Long id, Model model) {
        Optional<Color> optionalColor = colorService.findById(id);
        if (optionalColor.isPresent()) {
            Color color = optionalColor.get();
            model.addAttribute("Color", color);
            model.addAttribute("action", "/admin/color-update/" + color.getId());
            return "admin/color-create";
        } else {
            return "404";
        }
    }

    @GetMapping("/color-delete/{id}")
    public String delete(@PathVariable("id") Long id){
        colorService.delete(id);
        return "redirect:/admin/color-all";
    }
}
