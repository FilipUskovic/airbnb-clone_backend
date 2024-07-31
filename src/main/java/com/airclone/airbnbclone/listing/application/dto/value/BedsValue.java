package com.airclone.airbnbclone.listing.application.dto.value;

import jakarta.validation.constraints.NotNull;

public record BedsValue(
        @NotNull(message = "Beds value must be present")
        int value
) {
}
