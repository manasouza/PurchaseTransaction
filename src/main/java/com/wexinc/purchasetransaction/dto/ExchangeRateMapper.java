package com.wexinc.purchasetransaction.dto;

import com.wexinc.purchasetransaction.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper extends DefaultMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "record_date", target = "referenceDate", qualifiedByName = "stringToLocalDate")
    @Mapping(source = "effective_date", target = "effectiveDate", qualifiedByName = "stringToLocalDate")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "exchange_rate", target = "exchangeRate")
    ExchangeRate toEntity(ExchangeRateDto dto);

}
