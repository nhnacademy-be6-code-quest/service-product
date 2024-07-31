package com.nhnacademy.bookstoreinjun.controller;


import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.service.tag.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class TagController implements TagControllerInterface{
    private final TagService tagService;

    private final HttpHeaders header;

    @PostMapping("/admin/tag/register")
    public ResponseEntity<TagRegisterResponseDto> createTag(
            @Valid @RequestBody TagRegisterRequestDto tagRegisterRequestDto) {
        return new ResponseEntity<>(tagService.saveTag(tagRegisterRequestDto), header, HttpStatus.CREATED);
    }

    @PutMapping("/admin/tag/update")
    public ResponseEntity<TagUpdateResponseDto> updateTag(
            @Valid @RequestBody TagUpdateRequestDto tagUpdateRequestDto) {
        return new ResponseEntity<>(tagService.updateTag(tagUpdateRequestDto), header, HttpStatus.OK);
    }

    @DeleteMapping("/admin/tag/delete/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable("tagId") Long tagId) {
        return tagService.deleteTag(tagId);
    }


    @GetMapping("/admin/tags/all")
    public ResponseEntity<Page<TagGetResponseDto>> getAllTags(
            @Valid @ModelAttribute PageRequestDto pageRequestDto) {
        return new ResponseEntity<>(tagService.getAllTagPage(pageRequestDto), header, HttpStatus.OK);
    }


    @GetMapping("/admin/tags/containing")
    public ResponseEntity<Page<TagGetResponseDto>> getNameContainingTagPage(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @NotBlank @RequestParam("tagName") String tagName) {
        return new ResponseEntity<>(tagService.getNameContainingTagPage(pageRequestDto, tagName), header, HttpStatus.OK);
    }
}
