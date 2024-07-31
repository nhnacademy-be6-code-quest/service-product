package com.nhnacademy.bookstoreinjun.service.aladin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nhnacademy.bookstoreinjun.dto.book.aladin.AladinBookListResponseDto;
import com.nhnacademy.bookstoreinjun.dto.book.aladin.AladinBookResponseDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.exception.AladinJsonProcessingException;
import com.nhnacademy.bookstoreinjun.feignclient.AladinClient;
import com.nhnacademy.bookstoreinjun.util.PageableUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinServiceImpl implements AladinService {
    private final AladinClient aladinClient;

    private final XmlMapper xmlMapper = new XmlMapper();

    public Page<AladinBookResponseDto> getAladdinBookPage(PageRequestDto pageRequestDto, String title){
        try {
            byte[] bytes = title.getBytes(StandardCharsets.UTF_8);
            String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
            String responseBody = aladinClient.getBooks("ttbjasmine066220924001"
                    ,utf8EncodedString,
                    "Title",
                    "Big",
                    100);


            AladinBookListResponseDto responseDto = xmlMapper.readValue(responseBody, AladinBookListResponseDto.class);
            List<AladinBookResponseDto> aladinBookResponseDtoList = responseDto.getBooks();

            Pageable pageable = PageableUtil.makePageable(pageRequestDto, 4, "pubDate");


            int start = (int) pageable.getOffset();

            if (aladinBookResponseDtoList != null) {
                int end = Math.min((start + pageable.getPageSize()), aladinBookResponseDtoList.size());
                List<AladinBookResponseDto> pagedBookList = aladinBookResponseDtoList.subList(start, end);


                return new PageImpl<>(pagedBookList, pageable, aladinBookResponseDtoList.size());
            }else{
                return null;
            }
        }catch (JsonProcessingException e){
            throw new AladinJsonProcessingException(title);
        }
    }
}
