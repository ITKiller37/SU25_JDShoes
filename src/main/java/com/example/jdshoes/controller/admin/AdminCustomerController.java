package com.example.jdshoes.controller.admin;



import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminCustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customers")
    public String viewPage(Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "sort", defaultValue = "name,asc") String sortField,
                           @RequestParam(required = false) String search) {

        int pageSize = 10;
        String[] sortParams = sortField.split(",");
        String sortFieldName = sortParams[0];
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(sortDirection, sortFieldName);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<Customer> materialPage;

        if (search == null || search.trim().isEmpty()) {
            materialPage = customerRepository.findAllCustomerRoleUser(pageable);
        } else {
            String keyword = "%" + search.trim() + "%";
            materialPage = customerRepository.searchByParamAndRole(keyword, pageable);
        }

        model.addAttribute("items", materialPage);
        model.addAttribute("sortField", sortFieldName);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("search", (search == null) ? "" : search);

        return "admin/customer";
    }
}

