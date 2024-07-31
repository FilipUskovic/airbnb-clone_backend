package com.airclone.airbnbclone.listing.presentational;

import com.airclone.airbnbclone.listing.application.dto.CreatedListingDTO;
import com.airclone.airbnbclone.listing.application.dto.LandLordService;
import com.airclone.airbnbclone.listing.application.dto.SaveListingDTO;
import com.airclone.airbnbclone.listing.application.dto.sub.PictureDTO;
import com.airclone.airbnbclone.user.application.exception.UserException;
import com.airclone.airbnbclone.user.application.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/landlord-listing")
public class LandLordResources {
    private final LandLordService landLordService;
    private final Validator validator;
    private final UserService userService;
    private ObjectMapper mapper;

    public LandLordResources(LandLordService landLordService, Validator validator, UserService userService) {
        this.landLordService = landLordService;
        this.validator = validator;
        this.userService = userService;
    }

    public ResponseEntity<CreatedListingDTO> create(
            MultipartHttpServletRequest multipartHttpServletRequest,
            @RequestPart(name = "dto") String saveListingDTOString
    )throws IOException {
        List<PictureDTO> pictures = multipartHttpServletRequest.getFileMap()
                .values()
                .stream().map(mapMultiPartFileToPictureDTO())
                .toList();

        // deserilizacija
        SaveListingDTO saveListingDTO = mapper.readValue(saveListingDTOString, SaveListingDTO.class);
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
}
