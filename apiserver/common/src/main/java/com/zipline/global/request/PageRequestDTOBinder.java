package com.zipline.global.request;

import com.zipline.global.exception.page.PagingException;
import com.zipline.global.exception.page.errorcode.PagingErrorCode;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class PageRequestDTOBinder {

    @InitBinder("pageRequestDTO")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Map.class, "sortFields", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(parseSortFields(text));
            }
        });
    }

    private Map<String, Sort.Direction> parseSortFields(String text) {
        Map<String, Sort.Direction> sortFields = new LinkedHashMap<>();
        if (text == null || text.isEmpty()) {
            return sortFields;
        }
        String decodedText = URLDecoder.decode(text, StandardCharsets.UTF_8);
        String cleanedText = decodedText.replaceAll("[{}\"]", "");
        Arrays.stream(cleanedText.split(","))
                .forEach(field -> addSortField(sortFields, field.trim()));

        return sortFields;
    }

    private void addSortField(Map<String, Sort.Direction> sortFields, String field) {
        String[] parts = field.split(":");
        if (parts.length == 2) {
            try {
                String direction = parts[1].trim().toUpperCase().replace("\"", "");
                Sort.Direction sortDirection = Sort.Direction.valueOf(direction);
                sortFields.put(parts[0].trim(), sortDirection);
            } catch (IllegalArgumentException e) {
                throw new PagingException(PagingErrorCode.PAGING_SORT_ERROR);
            }
        } else {
            sortFields.put(field.trim(), Sort.Direction.ASC);
        }
    }
}
