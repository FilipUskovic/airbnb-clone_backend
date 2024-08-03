package com.airclone.airbnbclone.listing.repository;

import com.airclone.airbnbclone.listing.domain.BookingCategory;
import com.airclone.airbnbclone.listing.domain.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    @Query("select listing from Listing listing left join fetch listing.pictures picture" +
    " where listing.landlordPublicId =:landlordPublicId and picture.isCover= true")
    List<Listing> findAllByLandLordPublicIdFetchCoverPicture(UUID landlordPublicId);

    long deleteByPublicIdAndLandlordPublicId(UUID publicId, UUID landlordPublicId);
    // imamo pagable jer ne zelimo dohvatiti/vratiti cijeli database Listing
    @Query("SELECT listing from Listing listing left join fetch listing.pictures picture" +
            " where picture.isCover = true and listing.bookingCategory = :bookingCategory")
    Page<Listing> findAllByBookingCategoryWithCoverOnly(Pageable pageable, BookingCategory bookingCategory);

    // imamo ovu metodu jer  ne pocetku nece biti kategorija selektiranih
    @Query("SELECT listing from Listing listing left join fetch listing.pictures picture" +
            " where picture.isCover = true")
    Page<Listing> findAllWithCoverOnly(Pageable pageable);

    Optional<Listing> findByPublicId(UUID publicId);


}
