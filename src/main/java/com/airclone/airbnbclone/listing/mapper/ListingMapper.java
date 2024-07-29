package com.airclone.airbnbclone.listing.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ListingPictureMapper.class)
public interface ListingMapper {
}
