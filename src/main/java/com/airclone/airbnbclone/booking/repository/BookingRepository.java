package com.airclone.airbnbclone.booking.repository;

import com.airclone.airbnbclone.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
