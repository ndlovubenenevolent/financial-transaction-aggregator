package com.fintech.aggregator.aggregator.mapper;

import com.fintech.aggregator.aggregator.dto.TransactionResponse;
import com.fintech.aggregator.aggregator.entity.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);
}
