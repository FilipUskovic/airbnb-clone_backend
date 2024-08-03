package com.airclone.airbnbclone.listing.presentational;

import com.airclone.airbnbclone.listing.application.TenantService;
import com.airclone.airbnbclone.listing.application.dto.DisplayCardListingDTO;
import com.airclone.airbnbclone.listing.application.dto.DisplayListingDTO;
import com.airclone.airbnbclone.listing.domain.BookingCategory;
import com.airclone.airbnbclone.sharedkernel.service.State;
import com.airclone.airbnbclone.sharedkernel.service.StatusNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.ProblemDetail.forStatusAndDetail;

@RestController
@RequestMapping("/api/tenant-listing")
public class TenantResource {

    private final TenantService tenantService;

    public TenantResource(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/get-all-by-category")
    public ResponseEntity<Page<DisplayCardListingDTO>> findAllByBookingCategory(Pageable pageable, @RequestParam BookingCategory category) {
        return ResponseEntity.ok(tenantService.getAllCategory(pageable, category));
    }

    @GetMapping("/get-one")
    public ResponseEntity<DisplayListingDTO> getOne(@RequestParam UUID pubicId) {
        State<DisplayListingDTO, String> displayListingState = tenantService.getOne(pubicId);
        if(displayListingState.getStatus().equals(StatusNotification.OK)){
            return ResponseEntity.ok(displayListingState.getValue());
        }else {
            ProblemDetail problemDetail = forStatusAndDetail(HttpStatus.BAD_REQUEST, displayListingState.getError());
            return ResponseEntity.of(problemDetail).build();
        }
    }
}
