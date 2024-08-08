package com.airclone.airbnbclone.listing.application.dto;

import com.airclone.airbnbclone.booking.application.dto.BookedDateDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.ListingInfoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record SearchDTO(
        @Valid BookedDateDTO dates,
        @Valid ListingInfoDTO infos,
        @NotEmpty String location
        ) {
}
