package com.airclone.airbnbclone.booking.repository;

import com.airclone.airbnbclone.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT case when count(booking) > 0 then true else false end" +
            " from Booking booking WHERE NOT (booking.endDate <= :startDate or booking.startDate >= :endDate)" +
            " AND booking.fkListing = :fkListing")
    boolean bookingExistAtInterval(OffsetDateTime startDate, OffsetDateTime endDate, UUID fkListing);

    List<Booking> findAllByFkListing(UUID fkListing);

    List<Booking> findAllByFkTenant(UUID fkTenant);

    int deleteBookingByFkTenantAndPublicId(UUID tenantPublicId, UUID bookingPublicId);

    int deleteBookingByPublicIdAndFkListing(UUID bookingPublicId, UUID listingPublicId);

    List<Booking> findAllByFkListingIn(List<UUID> listOfPublicIds);

    @Query( "SELECT bookings FROM Booking bookings WHERE  " +
            " NOT(bookings.endDate <= :startDate or bookings.startDate >= :endDate)" +
           " AND bookings.fkListing IN :fkListings")
    List<Booking> findAllMatchWithDate(List<UUID> fkListings, OffsetDateTime startDate, OffsetDateTime endDate);
}
