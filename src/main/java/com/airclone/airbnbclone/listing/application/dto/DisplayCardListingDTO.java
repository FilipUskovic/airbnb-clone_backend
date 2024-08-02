package com.airclone.airbnbclone.listing.application.dto;

import com.airclone.airbnbclone.listing.application.dto.sub.PictureDTO;
import com.airclone.airbnbclone.listing.application.dto.value.PriceValue;
import com.airclone.airbnbclone.listing.domain.BookingCategory;

import java.util.UUID;

public record DisplayCardListingDTO(
        PriceValue price,
        String location,
        PictureDTO cover,
        BookingCategory bookingCategory,
        UUID publicId
) {
}
