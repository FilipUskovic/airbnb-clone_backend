package com.airclone.airbnbclone.listing.mapper;

import com.airclone.airbnbclone.listing.application.dto.sub.PictureDTO;
import com.airclone.airbnbclone.listing.domain.ListingPicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ListingPictureMapper {

    Set<ListingPicture> pictureDTOToListingPicture(List<PictureDTO> pictures);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "listing", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "cover", source = "isCover")
   ListingPicture pictureDTOToListingPicturee(PictureDTO pictureDTO);

    @Mapping(target = "isCover", source = "cover")
    PictureDTO convertToPictureDTO(ListingPicture listingPicture);

    List<PictureDTO> listingPictureToPictureDTO(List<ListingPicture> listingPictures);

    // return picture we extract in the cover
    @Named("extract-cover")
    default PictureDTO extractCover(Set<ListingPicture> pictures) {
        return pictures.stream().findFirst().map(this::convertToPictureDTO).orElseThrow();
    }

}
