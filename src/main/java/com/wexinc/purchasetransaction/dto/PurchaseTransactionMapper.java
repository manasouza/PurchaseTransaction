package com.wexinc.purchasetransaction.dto;

import com.wexinc.purchasetransaction.entity.PurchaseTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface PurchaseTransactionMapper extends DefaultMapper {

    PurchaseTransactionMapper INSTANCE = Mappers.getMapper(PurchaseTransactionMapper.class);

    @Mapping(source = "transactionDate", target = "transactionDate", qualifiedByName = "stringToLocalDate")
    @Mapping(source = "purchaseAmount", target = "purchaseAmount", qualifiedByName = "stringToBigDecimal")
    PurchaseTransaction toEntity(PurchaseTransactionRequest dto);

    @Mapping(source = "transactionDate", target = "transactionDate", qualifiedByName = "localDateToString")
    @Mapping(source = "purchaseAmount", target = "purchaseAmount", qualifiedByName = "bigDecimalToString")
    PurchaseTransactionResponse toDto(PurchaseTransaction entity);

    @Named("localDateToString")
    default String localDateToString(LocalDate date) {
        return date.toString();
    }

    @Named("bigDecimalToString")
    default String bigDecimalToString(BigDecimal value) {
        return value.toString();
    }

    @Named("stringToBigDecimal")
    default BigDecimal stringToBigDecimal(String value) {
        return new BigDecimal(value);
    }
}
