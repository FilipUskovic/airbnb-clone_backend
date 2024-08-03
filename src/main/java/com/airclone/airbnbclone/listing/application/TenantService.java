package com.airclone.airbnbclone.listing.application;

import com.airclone.airbnbclone.listing.application.dto.DisplayCardListingDTO;
import com.airclone.airbnbclone.listing.application.dto.DisplayListingDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.LandLordListingDTO;
import com.airclone.airbnbclone.listing.domain.BookingCategory;
import com.airclone.airbnbclone.listing.domain.Listing;
import com.airclone.airbnbclone.listing.mapper.ListingMapper;
import com.airclone.airbnbclone.listing.repository.ListingRepository;
import com.airclone.airbnbclone.sharedkernel.service.State;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.application.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class TenantService {

    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;
    private final UserService userService;

    public TenantService(ListingRepository listingRepository, ListingMapper listingMapper, UserService userService) {
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
        this.userService = userService;
    }

    public Page<DisplayCardListingDTO> getAllCategory(Pageable pageable, BookingCategory bookingCategory) {
        Page<Listing> allOrBookingCategory;
        if (bookingCategory == BookingCategory.ALL) {
            allOrBookingCategory = listingRepository.findAllWithCoverOnly(pageable);
        }else {
            allOrBookingCategory = listingRepository.findAllByBookingCategoryWithCoverOnly(pageable, bookingCategory);
        }
        return allOrBookingCategory.map(listingMapper::listingToDisplayCardListingDTO);
    }
    @Transactional(readOnly = true)
    public State<DisplayListingDTO, String> getOne(UUID publicId) {
        Optional<Listing> listingByPublicIdOpt = listingRepository.findByPublicId(publicId);
        if (listingByPublicIdOpt.isEmpty()) {
            return State.<DisplayListingDTO, String>builder().forError(String.format(
                    "Listing with id %s doesn not exist", publicId));
        }
        DisplayListingDTO displayListingDTO = listingMapper.listingToDisplayListingDTO(listingByPublicIdOpt.get());
        // da bi mogli dohvatiti informacij od landlord-a
        ReadUserDTO readUserDTO = userService.getByPublicId(listingByPublicIdOpt.get().getLandlordPublicId()).orElseThrow();
        LandLordListingDTO landLordListingDTO = new LandLordListingDTO(readUserDTO.firstName(), readUserDTO.imageUrl());
        displayListingDTO.setLandlord(landLordListingDTO);
        return State.<DisplayListingDTO, String>builder().forSuccess(displayListingDTO);
    }
}
