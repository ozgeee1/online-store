package com.book.onlinestore.service;

import com.book.onlinestore.dto.BookItem;
import com.book.onlinestore.dto.request.BookSaveRequest;
import com.book.onlinestore.dto.request.BookUpdateRequest;
import com.book.onlinestore.dto.response.BookResponse;
import com.book.onlinestore.entity.Book;
import com.book.onlinestore.exception.BookNotFoundException;
import com.book.onlinestore.mapper.BookMapper;
import com.book.onlinestore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * This method saves book if all the fields of BookSaveRequest is valid
     * Otherwise throws Validation exception
     * @param bookSaveRequest
     * @return BookResponse
     */
    public BookResponse saveBook(BookSaveRequest bookSaveRequest){
        Optional<Book> byTitleAndAuthor = bookRepository.findByTitleAndAuthor(bookSaveRequest.getTitle(), bookSaveRequest.getAuthor());
        if(byTitleAndAuthor.isEmpty()){
            Book savedBook = bookRepository.save(bookMapper.BookRequestToBookEntity(bookSaveRequest));
            return bookMapper.BookEntityToBookResponse(savedBook);
        }
        Book book = byTitleAndAuthor.get();
        book.setPrice(bookSaveRequest.getPrice());
        book.setStockQuantity(book.getStockQuantity()+bookSaveRequest.getStockQuantity());
        Book savedBook = bookRepository.save(book);
        return bookMapper.BookEntityToBookResponse(savedBook);
    }

    /**
     * This methods return List<BookResponse> given pageNumber and pageSize
     *pageNumber and pageSize are optional. default value for pageNumber is 0 , pageSize is 3
     * @param pageNumber
     * @param pageSize
     * @return List<BookResponse>
     */
    public List<BookResponse> getBooksByPagination(int pageNumber,int pageSize){
        pageNumber = Math.max(pageNumber, 0);
        pageSize =pageSize < 0 ? 3 : pageSize;
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdAt").descending());
        List<Book> allByOrderByCreatedAtDesc = bookRepository.findAllByOrderByCreatedAtDesc(pageable);
        return allByOrderByCreatedAtDesc.stream().map(bookMapper::BookEntityToBookResponse).collect(Collectors.toList());
    }

    /**
     * This method returns BookResponse given bookId
     * If there is no record for given bookId then throws BookNotFoundException
     * @param bookId
     * @return BookResponse
     */
    public BookResponse getBookById(UUID bookId){
        return bookMapper.BookEntityToBookResponse(getBookByIdOrElseThrowException(bookId));
    }

    /**
     * This method updates Book for given bookId
     * If there is no record for given bookId then throws BookNotFoundException
     * If there is no field to be updated then it doesn't apply any change
     * @param bookId
     * @param bookUpdateRequest
     * @return BookResponse
     */
    public BookResponse updateBook(UUID bookId, BookUpdateRequest bookUpdateRequest){
        Book bookInDB = getBookByIdOrElseThrowException(bookId);
        Book updatedBook = bookMapper.updateBookEntityFromBookUpdateRequest(bookUpdateRequest, bookInDB);
        Book savedBook = bookRepository.save(updatedBook);
        return bookMapper.BookEntityToBookResponse(savedBook);
    }

    /**
     * This method deleted book record for given bookId
     * If there is no record for given bookId then throws BookNotFoundException
     * @param bookId
     */

    public void deleteBook(UUID bookId){
        Book bookToBeDeleted = getBookByIdOrElseThrowException(bookId);
        bookRepository.delete(bookToBeDeleted);
    }

    /**
     * This method returns book record gor given bookId
     * If there is no record for given bookId then throws BookNotFoundException
     * @param bookId
     * @return Book
     */
    public Book getBookByIdOrElseThrowException(UUID bookId){
        Optional<Book> byId = bookRepository.findById(bookId);
        return byId.orElseThrow(() -> new BookNotFoundException("Book with id: "+ bookId.toString()+ " not found" ));
    }

    /**
     * This method converts BookItem to Book record and gives totalPrice
     * @param bookItems
     * @return HashMap<BigDecimal,List<Book>>
     */
    public HashMap<BigDecimal,List<Book>> getBookListFromBookItemListAndTotalPrice(List<BookItem> bookItems){
      BigDecimal totalPrice = BigDecimal.ZERO;
      List<Book> books = new ArrayList<>();
      HashMap<BigDecimal,List<Book>> resultMap = new HashMap<>();

      for(BookItem bookItem: bookItems){
          Book bookFromBookItem =getBookByIdOrElseThrowException(bookItem.getBookId());
          books.add(bookFromBookItem);
         totalPrice= totalPrice.add(bookFromBookItem.getPrice().multiply(BigDecimal.valueOf(bookItem.getQuantity())));
      }
      resultMap.put(totalPrice,books);
      return resultMap;

    }

    /**
     * This method after creating order , updates the stock quantity for book
     * @param bookId
     * @param quantity
     */
    @Transactional
    public void fixStockQuantity(UUID bookId,int quantity){
        Book book = getBookByIdOrElseThrowException(bookId);
        int newStock = book.getStockQuantity()-quantity;
        book.setStockQuantity(newStock);
        bookRepository.save(book);
    }


}
