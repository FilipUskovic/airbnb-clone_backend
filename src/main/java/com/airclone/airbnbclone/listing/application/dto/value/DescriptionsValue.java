package com.airclone.airbnbclone.listing.application.dto.value;

import jakarta.validation.constraints.NotNull;

public record DescriptionsValue(
        @NotNull(message = "Descriptions value must be present")
        String value
) {
}
