package com.airclone.airbnbclone.listing.application.dto;

import com.airclone.airbnbclone.listing.application.dto.sub.DescriptionDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.ListingInfoDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.PictureDTO;
import com.airclone.airbnbclone.listing.application.dto.value.PriceValue;
import com.airclone.airbnbclone.listing.domain.BookingCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SaveListingDTO {

    @NotNull
    BookingCategory bookingCategory;

    @NotNull
    String location;

    @NotNull
    @Valid
    ListingInfoDTO infos;

    @NotNull
    @Valid
    DescriptionDTO description;

    @NotNull
    @Valid
    PriceValue price;

    @NotNull
    List<PictureDTO> pictures;

    public @NotNull BookingCategory getBookingCategory() {
        return bookingCategory;
    }

    public void setBookingCategory(@NotNull BookingCategory bookingCategory) {
        this.bookingCategory = bookingCategory;
    }

    public @NotNull String getLocation() {
        return location;
    }

    public void setLocation(@NotNull String location) {
        this.location = location;
    }

    public @NotNull @Valid ListingInfoDTO getInfos() {
        return infos;
    }

    public void setInfos(@NotNull @Valid ListingInfoDTO infos) {
        this.infos = infos;
    }

    public @NotNull @Valid DescriptionDTO getDescription() {
        return description;
    }

    public void setDescription(@NotNull @Valid DescriptionDTO description) {
        this.description = description;
    }

    public @NotNull @Valid PriceValue getPrice() {
        return price;
    }

    public void setPrice(@NotNull @Valid PriceValue price) {
        this.price = price;
    }

    public @NotNull List<PictureDTO> getPictures() {
        return pictures;
    }

    public void setPictures(@NotNull List<PictureDTO> pictures) {
        this.pictures = pictures;
    }
}
