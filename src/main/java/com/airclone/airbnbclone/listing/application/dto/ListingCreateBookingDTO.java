package com.airclone.airbnbclone.listing.application.dto;

import com.airclone.airbnbclone.listing.application.dto.value.PriceValue;

import java.util.UUID;

public record ListingCreateBookingDTO(
        UUID listingPublicId, PriceValue price
) {
}
