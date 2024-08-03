package com.airclone.airbnbclone.listing.application;

import com.airclone.airbnbclone.listing.application.dto.DisplayCardListingDTO;
import com.airclone.airbnbclone.listing.domain.BookingCategory;
import com.airclone.airbnbclone.listing.domain.Listing;
import com.airclone.airbnbclone.listing.mapper.ListingMapper;
import com.airclone.airbnbclone.listing.repository.ListingRepository;
import com.airclone.airbnbclone.user.application.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
