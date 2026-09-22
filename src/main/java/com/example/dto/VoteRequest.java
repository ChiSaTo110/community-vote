package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class VoteRequest {
    private Long topicId;
    private List<Long> optionIds;
    private String fillText;
}