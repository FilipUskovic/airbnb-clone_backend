package com.airclone.airbnbclone.listing.application.dto;

import com.airclone.airbnbclone.listing.application.dto.sub.DescriptionDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.LandLordListingDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.ListingInfoDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.PictureDTO;
import com.airclone.airbnbclone.listing.application.dto.value.PriceValue;
import com.airclone.airbnbclone.listing.domain.BookingCategory;

import java.util.List;

public class DisplayListingDTO{

    private DescriptionDTO description;
    private List<PictureDTO> pictures;
    private ListingInfoDTO infos;
    private PriceValue price;
    private BookingCategory category;
    private String location;
    private LandLordListingDTO landlord;

    public DescriptionDTO getDescription() {
        return description;
    }

    public void setDescription(DescriptionDTO description) {
        this.description = description;
    }

    public List<PictureDTO> getPictures() {
        return pictures;
    }

    public void setPictures(List<PictureDTO> pictures) {
        this.pictures = pictures;
    }

    public ListingInfoDTO getInfos() {
        return infos;
    }

    public void setInfos(ListingInfoDTO infos) {
        this.infos = infos;
    }

    public PriceValue getPrice() {
        return price;
    }

    public void setPrice(PriceValue price) {
        this.price = price;
    }

    public BookingCategory getCategory() {
        return category;
    }

    public void setCategory(BookingCategory category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LandLordListingDTO getLandlord() {
        return landlord;
    }

    public void setLandlord(LandLordListingDTO landlord) {
        this.landlord = landlord;
    }
}
