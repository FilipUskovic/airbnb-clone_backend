package com.airclone.airbnbclone.listing.application.dto.value;

import jakarta.validation.constraints.NotNull;

public record BathsValue(
        @NotNull(message = "Baths value must be present")
        int value
        ) {
}
