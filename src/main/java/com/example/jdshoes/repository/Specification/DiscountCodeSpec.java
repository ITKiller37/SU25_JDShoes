package com.example.jdshoes.repository.Specification;

import com.example.jdshoes.dto.Discount.SearchDiscountCodeDto;
import com.example.jdshoes.entity.Discount;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DiscountCodeSpec implements Specification<Discount> {

    private SearchDiscountCodeDto searchDiscountCodeDto;

    public DiscountCodeSpec(SearchDiscountCodeDto searchRequest) {
        this.searchDiscountCodeDto = searchRequest;
    }

    @Override
    public Predicate toPredicate(Root<Discount> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        // Xử lý tìm kiếm theo keyword (code hoặc name)
        if (searchDiscountCodeDto.getKeyword() != null && !searchDiscountCodeDto.getKeyword().isEmpty()) {
            String keyword = "%" + searchDiscountCodeDto.getKeyword().toLowerCase() + "%";
            Predicate codePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), keyword);
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keyword);
            predicates.add(criteriaBuilder.or(codePredicate, namePredicate));
        }

        // Xử lý tìm kiếm theo code riêng
        if (searchDiscountCodeDto.getCode() != null && !searchDiscountCodeDto.getCode().isEmpty()) {
            Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + searchDiscountCodeDto.getCode().toLowerCase() + "%");
            predicates.add(predicate);
        }

        // Xử lý tìm kiếm theo name
        if (searchDiscountCodeDto.getName() != null && !searchDiscountCodeDto.getName().isEmpty()) {
            Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchDiscountCodeDto.getName().toLowerCase() + "%");
            predicates.add(predicate);
        }

        // Xử lý startDate
        if (searchDiscountCodeDto.getStartDate() != null) {
            Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), searchDiscountCodeDto.getStartDate());
            predicates.add(predicate);
        }

        // Xử lý endDate
        if (searchDiscountCodeDto.getEndDate() != null) {
            Predicate predicate = criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), searchDiscountCodeDto.getEndDate());
            predicates.add(predicate);
        }

        // Xử lý discountAmount
        if (searchDiscountCodeDto.getDiscountAmount() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("discountAmount"), searchDiscountCodeDto.getDiscountAmount());
            predicates.add(predicate);
        }

        // Xử lý percentage
        if (searchDiscountCodeDto.getPercentage() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("percentage"), searchDiscountCodeDto.getPercentage());
            predicates.add(predicate);
        }

        // Xử lý maximumAmount
        if (searchDiscountCodeDto.getMaximumAmount() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("maximumAmount"), searchDiscountCodeDto.getMaximumAmount());
            predicates.add(predicate);
        }

        // Xử lý minimumAmount
        if (searchDiscountCodeDto.getMinimumAmount() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("minimumAmount"), searchDiscountCodeDto.getMinimumAmount());
            predicates.add(predicate);
        }

        // Xử lý maximumUsage
        if (searchDiscountCodeDto.getMaximumUsage() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("maximumUsage"), searchDiscountCodeDto.getMaximumUsage());
            predicates.add(predicate);
        }

        // Xử lý note
        if (searchDiscountCodeDto.getNote() != null && !searchDiscountCodeDto.getNote().isEmpty()) {
            Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), "%" + searchDiscountCodeDto.getNote().toLowerCase() + "%");
            predicates.add(predicate);
        }

        // Xử lý discountCodeType (type)
        if (searchDiscountCodeDto.getDiscountCodeType() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("type"), searchDiscountCodeDto.getDiscountCodeType());
            predicates.add(predicate);
        }

        // Xử lý status
        if (searchDiscountCodeDto.getStatus() != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), searchDiscountCodeDto.getStatus());
            predicates.add(predicate);
        }

        // Thêm điều kiện deleteFlag = false (không xóa)
        predicates.add(criteriaBuilder.equal(root.get("deleteFlag"), false));

        // Kết hợp tất cả các predicate bằng AND
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
