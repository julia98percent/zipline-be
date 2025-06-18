package com.zipline.service.statics;

import io.micrometer.core.annotation.Timed;

public interface StaticsService {

  @Timed
  int getRecentContractCount(Long userId);

  @Timed
  int getOngoingContractCount(Long userId);

  @Timed
  int getCompletedContractCount(Long userId);

  @Timed
  int getRecentCustomerCount(Long userId);
}