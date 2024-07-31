package com.airclone.airbnbclone.listing.application.dto;

import com.airclone.airbnbclone.listing.domain.Listing;
import com.airclone.airbnbclone.listing.mapper.ListingMapper;
import com.airclone.airbnbclone.listing.repository.ListingRepository;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.application.service.Auth0Service;
import com.airclone.airbnbclone.user.application.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class LandLordService {
    private final ListingRepository listingRepository;
    private final ListingMapper mapper;
    private final UserService userService;
    private final Auth0Service auth0Service;
    private final PictureService pictureService;


    public LandLordService(ListingRepository listingRepository, ListingMapper mapper, UserService userService, Auth0Service auth0Service, PictureService pictureService) {
        this.listingRepository = listingRepository;
        this.mapper = mapper;
        this.userService = userService;
        this.auth0Service = auth0Service;
        this.pictureService = pictureService;
    }

    public CreatedListingDTO create(SaveListingDTO saveListingDTO) {
        Listing newListing = mapper.saveListingDTOToListing(saveListingDTO);
        ReadUserDTO userConnected = userService.getAuthenticatedUserFromSpringSecurity();
        newListing.setLandlordPublicId(userConnected.publicId());
        Listing savedListing = listingRepository.saveAndFlush(newListing);
        pictureService.saveAll(saveListingDTO.getPictures(), savedListing);
        auth0Service.addLandlordRoleToUser(userConnected);
        return mapper.listingToCreatedListingDTO(savedListing);

    }
}
