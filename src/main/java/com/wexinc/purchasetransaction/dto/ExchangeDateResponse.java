package com.wexinc.purchasetransaction.dto;

import java.util.List;

public class ExchangeDateResponse {

    private List<ExchangeRateDto> data;

    // Optionally include `meta` field if needed
    // private MetaDto meta;

    public List<ExchangeRateDto> getData() {
        return data;
    }

    public void setData(List<ExchangeRateDto> data) {
        this.data = data;
    }
}
