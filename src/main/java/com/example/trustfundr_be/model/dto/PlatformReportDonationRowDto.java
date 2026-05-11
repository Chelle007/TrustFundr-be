package com.example.trustfundr_be.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.trustfundr_be.model.Donation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlatformReportDonationRowDto {

    private String fundraisingActivityTitle;
    private BigDecimal amount;
    private Instant donatedAt;

    public static PlatformReportDonationRowDto fromEntity(Donation d) {
        PlatformReportDonationRowDto row = new PlatformReportDonationRowDto();
        row.setFundraisingActivityTitle(
                d.getFundraisingActivity() != null ? d.getFundraisingActivity().getTitle() : "—");
        row.setAmount(d.getAmount());
        row.setDonatedAt(d.getCreatedAt());
        return row;
    }
}
