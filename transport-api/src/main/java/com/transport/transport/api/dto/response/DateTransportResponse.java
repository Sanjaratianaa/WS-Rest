package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DateTransportResponse extends RepresentationModel<DateTransportResponse> {

    private Integer id;
    private LocalDate dateJour;
    private Boolean actif;
}
