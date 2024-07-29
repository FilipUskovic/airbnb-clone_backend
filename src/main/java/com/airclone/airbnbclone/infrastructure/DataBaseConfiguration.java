package com.airclone.airbnbclone.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({"com.airclone.airbnbclone.user.repository",
        "com.airclone.airbnbclone.listing.repository", "com.airclone.airbnbclone.booking.repository"})
@EnableTransactionManagement
@EnableJpaAuditing
public class DataBaseConfiguration {


}
