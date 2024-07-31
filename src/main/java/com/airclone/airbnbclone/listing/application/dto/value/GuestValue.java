package com.airclone.airbnbclone.listing.application.dto.value;

import jakarta.validation.constraints.NotNull;

public record GuestValue(
        @NotNull(message = "GuestValue value must be present")
        int value
) {
}
