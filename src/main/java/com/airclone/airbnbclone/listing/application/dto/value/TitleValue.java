package com.airclone.airbnbclone.listing.application.dto.value;

import jakarta.validation.constraints.NotNull;

public record TitleValue(
        @NotNull(message = "Title value must be present")
        int value
) {
}
