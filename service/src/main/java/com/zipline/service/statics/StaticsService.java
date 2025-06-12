package com.zipline.service.statics;

public interface StaticsService {
  int getRecentContractCount(Long userId);
  int getOngoingContractCount(Long userId);
  int getCompletedContractCount(Long userId);
  int getRecentCustomerCount(Long userId);
}
