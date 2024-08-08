package com.airclone.airbnbclone.booking.application;

import com.airclone.airbnbclone.booking.application.dto.BookedDateDTO;
import com.airclone.airbnbclone.booking.application.dto.BookedListingDTO;
import com.airclone.airbnbclone.booking.application.dto.NewBookingDTO;
import com.airclone.airbnbclone.booking.domain.Booking;
import com.airclone.airbnbclone.booking.mapper.BookingMapper;
import com.airclone.airbnbclone.booking.repository.BookingRepository;
import com.airclone.airbnbclone.infrastructure.config.SecurityUtils;
import com.airclone.airbnbclone.listing.application.LandLordService;
import com.airclone.airbnbclone.listing.application.dto.DisplayCardListingDTO;
import com.airclone.airbnbclone.listing.application.dto.ListingCreateBookingDTO;
import com.airclone.airbnbclone.listing.application.dto.value.PriceValue;
import com.airclone.airbnbclone.sharedkernel.service.State;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.application.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final LandLordService landLordService;


    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, UserService userService, LandLordService landLordService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.landLordService = landLordService;
    }

    @Transactional
    public State<Void, String> create(NewBookingDTO newBookingDTO) {
        Booking booking = bookingMapper.newBookingToBooking(newBookingDTO);
        Optional<ListingCreateBookingDTO> listingOpt = landLordService.getByListingPublicId(newBookingDTO.listingPublicId());
        if(listingOpt.isEmpty()){
            return State.<Void, String>builder().forError("LandLord publicId not found");
        }
        boolean alreadyBooked = bookingRepository.bookingExistAtInterval(newBookingDTO.startDate(),
                newBookingDTO.endDate(), newBookingDTO.listingPublicId());
        if (alreadyBooked){
            return State.<Void, String>builder().forError("One Booking already exist");
        }
        ListingCreateBookingDTO listingCreateBookingDTO = listingOpt.get();
        // jer nije mapiran
        booking.setFkListing(listingCreateBookingDTO.listingPublicId());

        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSpringSecurity();
        booking.setFkTenant(connectedUser.publicId());
        booking.setNumberOfTravelers(1);
        long numbersOfNights = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        booking.setTotalPrice((int) (numbersOfNights * listingCreateBookingDTO.price().value()));

        bookingRepository.save(booking);

        return State.<Void,String>builder().forSuccess();
    }

    @Transactional(readOnly = true)
    public List<BookedDateDTO> checkAvailability(UUID publicId){
        return bookingRepository.findAllByFkListing(publicId)
                .stream().map(bookingMapper::bookingToCheckAvailability).toList();
    }
    @Transactional(readOnly = true)
    public List<BookedListingDTO> getBookedListings(){
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSpringSecurity();
        List<Booking> allBookings = bookingRepository.findAllByFkTenant(connectedUser.publicId());
        //extraktam sve listingId
        List<UUID> allListingPublicId = allBookings.stream().map(Booking::getFkListing).toList();
        List<DisplayCardListingDTO> allListings = landLordService.getCardDisplayByListingPublicId(allListingPublicId);
        // pokusavm mergati sve bookinge i sve listinge
         return mapBookingToBookedListing(allBookings, allListings);

    }

    private List<BookedListingDTO> mapBookingToBookedListing(List<Booking> allBookings, List<DisplayCardListingDTO> allListings) {
        return allBookings.stream().map(booking -> {
            DisplayCardListingDTO displayCardListingDTO = allListings.stream()
                    .filter(listing -> listing.publicId().equals(booking.getFkListing()))
                    .findFirst()
                    .orElseThrow();
            BookedDateDTO dates = bookingMapper.bookingToCheckAvailability(booking);
            return new BookedListingDTO(displayCardListingDTO.cover(),
                                        displayCardListingDTO.location(),
                                        dates,
                                        new PriceValue(booking.getTotalPrice()),
                                        booking.getPublicId(), displayCardListingDTO.publicId());
        }).toList();
    }

    @Transactional
    public State<UUID, String> cancelReservation(UUID bookingPublicId, UUID listingPublicId, boolean byLandlord) {
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSpringSecurity();
     //   int deleteSuccess = bookingRepository.deleteBookingByFkTenantAndPublicId(connectedUser.publicId(), bookingPublicId);
        // modificirati cemo ovo da moze i landlord i user cancelat rezervaciju
        int deleteSuccess = 0;

        if (SecurityUtils.hasCurrentUserAnyOfAuthorites(SecurityUtils.ROLE_LANDLORD)
                && byLandlord) {
            deleteSuccess = handleDeletionForLandLord(bookingPublicId, listingPublicId, connectedUser, deleteSuccess);
        } else {
            deleteSuccess = bookingRepository.deleteBookingByFkTenantAndPublicId(connectedUser.publicId(), bookingPublicId);
        }

        if (deleteSuccess >= 1) {
            return State.<UUID, String>builder().forSuccess(bookingPublicId);
        } else {
            return State.<UUID, String>builder().forError("Booking not found");
        }
    }

    private int handleDeletionForLandLord(UUID bookingPublicId, UUID listingPublicId, ReadUserDTO connectedUser, int deleteSuccess) {
        Optional<DisplayCardListingDTO> listingVerificationOpt = landLordService.getByPublicIdAndLandlordPublicId(listingPublicId, connectedUser.publicId());
        if (listingVerificationOpt.isPresent()) {
            deleteSuccess = bookingRepository.deleteBookingByPublicIdAndFkListing(bookingPublicId, listingVerificationOpt.get().publicId());
        }
        return deleteSuccess;
    }

    @Transactional(readOnly = true)
    public List<BookedListingDTO> getBookedListingForLandLord(){
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSpringSecurity();
        List<DisplayCardListingDTO> allProperties = landLordService.getAllProperties(connectedUser);
        List<UUID> listOfPublicIds = allProperties.stream().map(DisplayCardListingDTO::publicId).toList();
        List<Booking> allBookings = bookingRepository.findAllByFkListingIn(listOfPublicIds);
        return mapBookingToBookedListing(allBookings, allProperties);
    }

    public List<UUID> getBookingMatchByListingIdsAndBookedDates(List<UUID> listingsId, BookedDateDTO bookedDates){
        return bookingRepository.findAllMatchWithDate(listingsId, bookedDates.startDate(), bookedDates.endDate())
                .stream().map(Booking::getFkListing).toList(); // mapiram samo po FkListingu jer me nije briga za ostalo

    }

}
