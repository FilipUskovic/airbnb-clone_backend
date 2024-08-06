package com.airclone.airbnbclone.booking.mapper;

import com.airclone.airbnbclone.booking.application.dto.BookedDateDTO;
import com.airclone.airbnbclone.booking.application.dto.NewBookingDTO;
import com.airclone.airbnbclone.booking.domain.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking newBookingToBooking(NewBookingDTO newBookingDTO);

    BookedDateDTO bookingToCheckAvailability(Booking booking);
}
