package com.assist.Internship_2024_java_yellow.mappers;

import com.assist.Internship_2024_java_yellow.entities.Bid;
import com.assist.Internship_2024_java_yellow.dtos.BidsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BidMapper {

    BidMapper INSTANCE = Mappers.getMapper(BidMapper.class);

    BidsDTO bidsToBidsDTO(Bid bid);
}
