package com.sih.DepartmentStaffService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
        List<T> content;
        int page;
        int size;
        int totalPages;
        boolean last;

        public PageResponse(Page<T> incomingPage) {
            this.content = incomingPage.getContent();
            this.page = incomingPage.getNumber();
            this.size = incomingPage.getSize();
            this.totalPages = incomingPage.getTotalPages();
            this.last = incomingPage.isLast();
        }
}
