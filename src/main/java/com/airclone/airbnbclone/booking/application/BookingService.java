package com.airclone.airbnbclone.booking.application;

import com.airclone.airbnbclone.booking.application.dto.BookedDateDTO;
import com.airclone.airbnbclone.booking.application.dto.BookedListingDTO;
import com.airclone.airbnbclone.booking.application.dto.NewBookingDTO;
import com.airclone.airbnbclone.booking.domain.Booking;
import com.airclone.airbnbclone.booking.mapper.BookingMapper;
import com.airclone.airbnbclone.booking.repository.BookingRepository;
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

    public State<UUID, String> cancelReservation(UUID bookingPublicId, UUID listingPublicId) {
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSpringSecurity();
        int deleteSuccess = bookingRepository.deleteBookingByFkTenantAndPublicId(connectedUser.publicId(), bookingPublicId);
        if(deleteSuccess >= 1){
            return State.<UUID, String>builder().forSuccess(bookingPublicId);
        }else {
            return State.<UUID, String>builder().forError("Booking not found");
        }
    }

}
