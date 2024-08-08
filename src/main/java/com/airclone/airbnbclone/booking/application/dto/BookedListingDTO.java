package com.airclone.airbnbclone.booking.application.dto;

import com.airclone.airbnbclone.listing.application.dto.sub.PictureDTO;
import com.airclone.airbnbclone.listing.application.dto.value.PriceValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookedListingDTO(
        @Valid PictureDTO cover,
        @NotEmpty String location,
        @Valid BookedDateDTO dates,
        @Valid PriceValue totalPrice,
        @NotNull UUID bookingPublicId,
        @NotNull UUID listingPublicId
        ) {
}
