package com.airclone.airbnbclone.listing.application.dto.sub;

import com.airclone.airbnbclone.listing.application.dto.value.BathsValue;
import com.airclone.airbnbclone.listing.application.dto.value.BedroomsValue;
import com.airclone.airbnbclone.listing.application.dto.value.BedsValue;
import com.airclone.airbnbclone.listing.application.dto.value.GuestValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ListingInfoDTO(
        @NotNull @Valid GuestValue guests,
        @NotNull @Valid BedroomsValue bedrooms,
        @NotNull @Valid BedsValue beds,
        @NotNull @Valid BathsValue baths
        ) {
}
