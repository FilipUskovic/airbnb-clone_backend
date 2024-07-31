package com.airclone.airbnbclone.listing.application.dto.value;

import jakarta.validation.constraints.NotNull;

public record BedroomsValue(
        @NotNull(message = "Bedrooms value must be present")
        int value

) {
}
