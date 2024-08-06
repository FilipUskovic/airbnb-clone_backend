package com.airclone.airbnbclone.listing.application;

import com.airclone.airbnbclone.listing.application.dto.CreatedListingDTO;
import com.airclone.airbnbclone.listing.application.dto.DisplayCardListingDTO;
import com.airclone.airbnbclone.listing.application.dto.ListingCreateBookingDTO;
import com.airclone.airbnbclone.listing.application.dto.SaveListingDTO;
import com.airclone.airbnbclone.listing.domain.Listing;
import com.airclone.airbnbclone.listing.mapper.ListingMapper;
import com.airclone.airbnbclone.listing.repository.ListingRepository;
import com.airclone.airbnbclone.sharedkernel.service.State;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.application.service.Auth0Service;
import com.airclone.airbnbclone.user.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LandLordService {
    private static final Logger log = LoggerFactory.getLogger(LandLordService.class);
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

    @Transactional(readOnly = true)
    public List<DisplayCardListingDTO> getAllProperties(ReadUserDTO landlord) {
        List<Listing> properties = listingRepository.findAllByLandLordPublicIdFetchCoverPicture(landlord.publicId());
        return mapper.listingToDisplayCardListingDTOs(properties);
    }

    // Posto se brise nije samo readonly jel logicno
    @Transactional
    public State<UUID, String> delete(UUID publicId, ReadUserDTO landlord) {
        // ovjde radimo 2 parametra jer zelim osigurati sam da landlord koji sadrzi listing moze ga i obrisati
        long deletedSuccessfully = listingRepository.deleteByPublicIdAndLandlordPublicId(publicId,landlord.publicId());
        if (deletedSuccessfully > 0) {
            return State.<UUID, String>builder().forSuccess(publicId);
        } else {
            return State.<UUID,String>builder().forUnauthorized("User not authorized to delete  this listing");
        }
    }

    public Optional<ListingCreateBookingDTO> getByListingPublicId(UUID publicId){
            return listingRepository.findByPublicId(publicId).map(mapper::listingToListingCreateBookingDTO);

    }
}
