package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.Transaction;
import com.fintech.aggregator.aggregator.repository.TransactionRepository;
import com.fintech.aggregator.common.enums.TransactionCategory;
import com.fintech.aggregator.common.enums.TransactionSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionQueryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionQueryService queryService;

    @Test
    void shouldDelegateFindAll() {
        Page<Transaction> page = new PageImpl<>(List.of());
        when(transactionRepository.findAll(Pageable.unpaged())).thenReturn(page);
        assertThat(queryService.findAll(Pageable.unpaged())).isSameAs(page);
    }

    @Test
    void shouldDelegateFindByCustomerId() {
        Page<Transaction> page = new PageImpl<>(List.of());
        when(transactionRepository.findByCustomerId(eq("CUST-001"), eq(Pageable.unpaged()))).thenReturn(page);
        assertThat(queryService.findByCustomerId("CUST-001", Pageable.unpaged())).isSameAs(page);
    }

    @Test
    void shouldDelegateFindByCategory() {
        Page<Transaction> page = new PageImpl<>(List.of());
        when(transactionRepository.findByCategory(eq(TransactionCategory.GROCERIES), eq(Pageable.unpaged()))).thenReturn(page);
        assertThat(queryService.findByCategory(TransactionCategory.GROCERIES, Pageable.unpaged())).isSameAs(page);
        verify(transactionRepository).findByCategory(TransactionCategory.GROCERIES, Pageable.unpaged());
    }

    @Test
    void shouldDelegateFindBySource() {
        Page<Transaction> page = new PageImpl<>(List.of());
        when(transactionRepository.findBySource(eq(TransactionSource.BANK), eq(Pageable.unpaged()))).thenReturn(page);
        assertThat(queryService.findBySource(TransactionSource.BANK, Pageable.unpaged())).isSameAs(page);
    }
}
