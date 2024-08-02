package com.airclone.airbnbclone.listing.repository;

import com.airclone.airbnbclone.listing.domain.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    @Query("select listing from Listing listing left join fetch listing.pictures picture" +
    " where listing.landlordPublicId =:landlordPublicId and picture.isCover= true")
    List<Listing> findAllByLandLordPublicIdFetchCoverPicture(UUID landlordPublicId);

    long deleteByPublicIdAndLandlordPublicId(UUID publicId, UUID landlordPublicId);
}
