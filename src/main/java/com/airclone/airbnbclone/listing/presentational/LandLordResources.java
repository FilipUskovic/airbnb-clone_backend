package com.airclone.airbnbclone.listing.presentational;

import com.airclone.airbnbclone.infrastructure.config.SecurityUtils;
import com.airclone.airbnbclone.listing.application.dto.CreatedListingDTO;
import com.airclone.airbnbclone.listing.application.LandLordService;
import com.airclone.airbnbclone.listing.application.dto.DisplayCardListingDTO;
import com.airclone.airbnbclone.listing.application.dto.SaveListingDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.PictureDTO;
import com.airclone.airbnbclone.sharedkernel.service.State;
import com.airclone.airbnbclone.sharedkernel.service.StatusNotification;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.application.exception.UserException;
import com.airclone.airbnbclone.user.application.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/landlord-listing")
public class LandLordResources {
    private final LandLordService landLordService;
    private final Validator validator;
    private final UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public LandLordResources(LandLordService landLordService, Validator validator, UserService userService) {
        this.landLordService = landLordService;
        this.validator = validator;
        this.userService = userService;
    }
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreatedListingDTO> create(
            MultipartHttpServletRequest multipartHttpServletRequest,
            @RequestPart(name = "dto") String saveListingDTOString
    )throws IOException {
        List<PictureDTO> pictures = multipartHttpServletRequest.getFileMap()
                .values()
                .stream().map(mapMultiPartFileToPictureDTO())
                .toList();

        // deserilizacija
        SaveListingDTO saveListingDTO = objectMapper.readValue(saveListingDTOString, SaveListingDTO.class);
        saveListingDTO.setPictures(pictures);
        // da budemo sigurni da je validatn validitrati cemo
        Set<ConstraintViolation<SaveListingDTO>> violations = validator.validate(saveListingDTO);
        if(!violations.isEmpty()) {
            String voiolationJoined = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining());
            // generirati za posalati na front
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, voiolationJoined);
            return ResponseEntity.of(problemDetail).build();
        }else{
            return ResponseEntity.ok(landLordService.create(saveListingDTO));
        }
    }

    private static Function<MultipartFile, PictureDTO> mapMultiPartFileToPictureDTO() {
        return multipartFile -> {
            try {
                return new PictureDTO(multipartFile.getBytes(), multipartFile.getContentType(), false);
            }catch (IOException e) {
                throw new UserException(e.getMessage());
            }
        };
    }
    @GetMapping("/get-all")
    @PreAuthorize("hasAnyRole('" + SecurityUtils.ROLE_LANDLORD + "')")
    public ResponseEntity<List<DisplayCardListingDTO>> getAll(){
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSpringSecurity();
        List<DisplayCardListingDTO> allProperties = landLordService.getAllProperties(connectedUser);
        return ResponseEntity.ok(allProperties);
    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('" + SecurityUtils.ROLE_LANDLORD + "')")
    public ResponseEntity<UUID> delete (@RequestParam UUID publicId){
        ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSpringSecurity();
        State<UUID, String> deleteState = landLordService.delete(publicId, connectedUser);
        if (deleteState.getStatus().equals(StatusNotification.OK)) {
            return ResponseEntity.ok(deleteState.getValue());
        }else if (deleteState.getStatus().equals(StatusNotification.UNAUTHORIZED)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
