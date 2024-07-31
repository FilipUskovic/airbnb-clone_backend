package com.airclone.airbnbclone.listing.application.dto.sub;

import com.airclone.airbnbclone.listing.application.dto.value.DescriptionsValue;
import com.airclone.airbnbclone.listing.application.dto.value.TitleValue;
import jakarta.validation.constraints.NotNull;

public record DescriptionDTO(
        @NotNull
        TitleValue title,
        @NotNull
        DescriptionsValue description

) {
}
