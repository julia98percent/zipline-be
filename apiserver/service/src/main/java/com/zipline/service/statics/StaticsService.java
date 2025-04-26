package com.zipline.service.statics;

public interface StaticsService {
  long getRecentContractCount(Long userId);
  long getOngoingContractCount(Long userId);
  long getCompletedContractCount(Long userId);
  long getRecentCustomerCount(Long userId);
}
