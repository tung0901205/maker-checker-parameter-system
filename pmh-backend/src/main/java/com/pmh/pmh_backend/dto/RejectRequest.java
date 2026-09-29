package com.pmh.pmh_backend.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class RejectRequest {
  private List<Long> ids;
  private String reason;
}