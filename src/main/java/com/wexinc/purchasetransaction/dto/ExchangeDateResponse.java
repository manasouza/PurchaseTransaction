package com.wexinc.purchasetransaction.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ExchangeDateResponse {

    private List<ExchangeRateDto> data;
    private ExternalMeta meta;
    private ExternalLinks links;

    @Getter
    public static class ExternalMeta {
        private int count;
        private int totalCount;   // note JSON “total‑count”
        private int totalPages;
    }

    @Getter
    public static class ExternalLinks {
        private String self;
        private String first;
        private String prev;
        private String next;
        private String last;
    }
}
