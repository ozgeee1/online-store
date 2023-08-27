package com.book.onlinestore.mapper;

import com.book.onlinestore.dto.BookItem;
import com.book.onlinestore.dto.request.BookSaveRequest;
import com.book.onlinestore.dto.request.BookUpdateRequest;
import com.book.onlinestore.dto.response.BookResponse;
import com.book.onlinestore.entity.Book;
import com.book.onlinestore.exception.BookNotFoundException;
import com.book.onlinestore.repository.BookRepository;
import com.book.onlinestore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class BookMapper {

    @Autowired
    private  BookRepository bookRepository;

    public abstract Book BookRequestToBookEntity(BookSaveRequest bookSaveRequest);

    @Mapping(source ="title",target = "title")
    public abstract BookResponse BookEntityToBookResponse(Book book);

    public abstract Book updateBookEntityFromBookUpdateRequest(BookUpdateRequest bookUpdateRequest,@MappingTarget Book book);

}
