package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSynchronizationListenerTest {

  private final static String VALID_ISBN = "1234567891234";
  private final static String BOOK_TITLE = "Java Book";

  @Mock
  private BookRepository bookRepository;

  @Mock
  private OpenLibraryApiClient openLibraryApiClient;

  @InjectMocks
  private BookSynchronizationListener cut;

  @Captor
  private ArgumentCaptor<Book> bookArgumentCaptor;

  @Test
  @DisplayName("Should reject book when ISBN is malformed")
  void shouldRejectBookWhenIsbnIsMalformed() {

    //Given
    BookSynchronization bookSynchronization = new BookSynchronization("42");

    //When
    cut.consumeBookUpdates(bookSynchronization);

    //Then
    verifyNoInteractions(openLibraryApiClient, bookRepository);

  }

  @Test
  @DisplayName("Should not override when book already exists")
  void shouldNotOverrideWhenBookAlreadyExists() {

    //Given
    BookSynchronization bookSynchronization = new BookSynchronization(VALID_ISBN);
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(new Book());

    //When
    cut.consumeBookUpdates(bookSynchronization);

    //Then
    verifyNoInteractions(openLibraryApiClient);
    verify(bookRepository, times(0)).save(ArgumentMatchers.any());

  }

  @Test
  @DisplayName("Should throw exception when processing fails")
  void shouldThrowExceptionWhenProcessingFails() {

    //Given
    BookSynchronization bookSynchronization = new BookSynchronization(VALID_ISBN);
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(null);
    when(openLibraryApiClient.fetchMetadataForBook(VALID_ISBN)).thenThrow(new RuntimeException("Network timeout"));

    //When
    assertThrows(RuntimeException.class, () -> cut.consumeBookUpdates(bookSynchronization));

  }

  @Test
  @DisplayName("Should store book when new and correct ISBN")
  void shouldStoreBookWhenNewAndCorrectIsbn() {

    //Given
    BookSynchronization bookSynchronization = new BookSynchronization(VALID_ISBN);
    when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(null);

    Book result = new Book();
    result.setTitle(BOOK_TITLE);
    result.setIsbn(VALID_ISBN);

    when(openLibraryApiClient.fetchMetadataForBook(VALID_ISBN)).thenReturn(result);
    when(bookRepository.save(any())).then( invocationOnMock -> {
      Book methodArgument = invocationOnMock.getArgument(0);
      methodArgument.setId(1L);
      return methodArgument;
    });

    //When
    cut.consumeBookUpdates(bookSynchronization);

    //Then
    verify(bookRepository).save(bookArgumentCaptor.capture());
    assertEquals(BOOK_TITLE, bookArgumentCaptor.getValue().getTitle());
    assertEquals(VALID_ISBN,          bookArgumentCaptor.getValue().getIsbn());

  }

}
