package com.zipline.global.request;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import lombok.Setter;

@Setter
public class PageRequestDTO {
	private int page;
	private int size;
  private Map<String, Direction> sortFields = new LinkedHashMap<>();
	
	private int getPage() {
		if (this.page <= 0) {
			return 0;
		}
		return this.page - 1;
	}
	
	private Integer getSize() {
		if (this.size <= 0 || this.size > 100) {
			return 20;
		}
		return size;
	}
	
	private Sort getSort() {
		if (sortFields == null || sortFields.isEmpty()) {
			return Sort.unsorted();
		}
		
		List<Order> orders = new ArrayList<>();
		for (Map.Entry<String, Direction> entry : sortFields.entrySet()) {
			orders.add(new Order(entry.getValue(), entry.getKey()));
		}
		
		return Sort.by(orders);
	}

  public Pageable toPageable() {
		return PageRequest.of(getPage(), getSize(), getSort());
	}
	
	public void addSortField(String field) {
		addSortField(field, Direction.ASC);
	}

	public void addSortField(String field, Direction direction) {
		if (sortFields == null) {
			sortFields = new LinkedHashMap<>();
		}
		sortFields.put(field, direction);
	}
}
