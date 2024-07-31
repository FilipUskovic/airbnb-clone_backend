package com.airclone.airbnbclone.listing.application.dto.value;

import jakarta.validation.constraints.NotNull;

public record PriceValue(
        @NotNull(message = "Price value must be present")
        int value
) {
}
