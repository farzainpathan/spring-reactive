package com.example.springreactive.modal;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ProductEvent {

    private Long eventId;

    private String eventType;


}
