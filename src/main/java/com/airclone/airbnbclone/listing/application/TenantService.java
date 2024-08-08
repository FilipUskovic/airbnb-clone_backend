package com.airclone.airbnbclone.listing.application;

import com.airclone.airbnbclone.booking.application.BookingService;
import com.airclone.airbnbclone.listing.application.dto.DisplayCardListingDTO;
import com.airclone.airbnbclone.listing.application.dto.DisplayListingDTO;
import com.airclone.airbnbclone.listing.application.dto.SearchDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.LandLordListingDTO;
import com.airclone.airbnbclone.listing.domain.BookingCategory;
import com.airclone.airbnbclone.listing.domain.Listing;
import com.airclone.airbnbclone.listing.mapper.ListingMapper;
import com.airclone.airbnbclone.listing.repository.ListingRepository;
import com.airclone.airbnbclone.sharedkernel.service.State;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.application.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantService {

    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;
    private final UserService userService;
    private final BookingService bookingService;

    public TenantService(ListingRepository listingRepository, ListingMapper listingMapper, UserService userService, BookingService bookingService) {
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
        this.userService = userService;
        this.bookingService = bookingService;
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
            return State.<DisplayListingDTO, String>builder()
                    .forError(String.format("Listing doesn't exist for publicId: %s", publicId));
        }

        DisplayListingDTO displayListingDTO = listingMapper.listingToDisplayListingDTO(listingByPublicIdOpt.get());

        ReadUserDTO readUserDTO = userService.getByPublicId(listingByPublicIdOpt.get().getLandlordPublicId()).orElseThrow();
        LandLordListingDTO landlordListingDTO = new LandLordListingDTO(readUserDTO.firstName(), readUserDTO.imageUrl());

        displayListingDTO.setLandlord(landlordListingDTO);
        return State.<DisplayListingDTO, String>builder().forSuccess(displayListingDTO);
    }

    @Transactional(readOnly = true)
    public Page<DisplayCardListingDTO> search(Pageable pageable, SearchDTO newSearch) {
        Page<Listing> allMatchListings = listingRepository.findAllByLocationAndBathroomsAndBedroomsAndGuestsAndBeds(pageable, newSearch.location(), newSearch.infos().baths().value(),
                newSearch.infos().bedrooms().value(), newSearch.infos().guests().value(), newSearch.infos().beds().value());
        List<UUID> listingUUIDs = allMatchListings.stream().map(Listing::getPublicId).toList();
        List<UUID> bookingUUIDS = bookingService.getBookingMatchByListingIdsAndBookedDates(listingUUIDs, newSearch.dates());
        List<DisplayCardListingDTO> listingsNotBooked = allMatchListings.stream().filter(listing -> !bookingUUIDS.contains(listing.getPublicId()))
                .map(listingMapper::listingToDisplayCardListingDTO).toList();
        return new PageImpl<>(listingsNotBooked, pageable, listingUUIDs.size());
    }
}
