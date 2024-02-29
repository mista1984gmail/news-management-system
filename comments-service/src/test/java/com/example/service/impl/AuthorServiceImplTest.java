package com.example.service.impl;

import com.example.entity.Author;
import com.example.entity.dto.AuthorDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.AuthorMapper;
import com.example.repository.AuthorRepository;
import com.example.util.AuthorTestData;
import com.example.util.ConstantsForTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {


    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void shouldDeleteAuthor(){
        // given
        Long id = ConstantsForTest.AUTHOR_ID;
        Optional<Author> author = Optional.of(AuthorTestData.builder()
                .build()
                .buildAuthor());

        when(authorRepository.findById(id))
                .thenReturn(author);
        doNothing()
                .when(authorRepository)
                .delete(author.get());

        //when
        authorService.deleteById(id);

        //then
        verify(authorRepository).delete(any());
    }

    @Test
    void shouldBlockedAuthor(){
        // given
        Long id = ConstantsForTest.AUTHOR_ID;
        Author author = AuthorTestData.builder()
                .withIsBlocked(true)
                .build()
                .buildAuthor();
        Optional<Author> optionalAuthorFromDB = Optional.of(AuthorTestData.builder()
                .build()
                .buildAuthor());

        when(authorRepository.findById(id)).thenReturn(optionalAuthorFromDB);
        when(authorRepository.save(author))
                .thenReturn(author);

        //when
        authorService.blocked(id);

        //then
        verify(authorRepository).save(any());
    }

    @Test
    void shouldNotDeleteAuthorAndThrowsAuthorNotFoundException() {
        // given
        Long id = ConstantsForTest.AUTHOR_ID;
        String errorMessage = "Author with id: " + id + " not found";
        Optional<Author> author = Optional.of(AuthorTestData.builder()
                .withId(null)
                .build()
                .buildAuthor());

        when(authorRepository.findById(id))
                .thenThrow(new EntityNotFoundException(Author.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            authorService.deleteById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
        verify(authorRepository, never()).delete(author.get());
    }

    @Test
    void shouldNotGetAuthorByIdAndThrowsAuthorNotFoundException() {
        // given
        Long id = ConstantsForTest.AUTHOR_ID;
        String errorMessage = "Author with id: " + id + " not found";

        when(authorRepository.findById(id))
                .thenThrow(new EntityNotFoundException(Author.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            authorService.findById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldGetAuthorById() {
        // given
        Long id = ConstantsForTest.AUTHOR_ID;
        Optional<Author> author = Optional.of(AuthorTestData.builder()
                .build()
                .buildAuthor());
        AuthorDto expected = AuthorTestData.builder()
                .build()
                .buildAuthorDto();

        when(authorRepository.findById(id))
                .thenReturn(author);
        when(authorMapper.entityToDto(author.get()))
                .thenReturn(expected);

        //when
        AuthorDto actual = authorService.findById(id);

        //then
        assertEquals(expected, actual);
        verify(authorRepository).findById(id);
        verify(authorMapper).entityToDto(author.get());
    }

    @Test
    void shouldGetAuthorByUsername() {
        // given
        String username = ConstantsForTest.AUTHOR_USERNAME;
        Optional<Author> author = Optional.of(AuthorTestData.builder()
                .build()
                .buildAuthor());
        Author expected = AuthorTestData.builder()
                .build()
                .buildAuthor();

        when(authorRepository.findByUsername(username))
                .thenReturn(author);

        //when
        Author actual = authorService.findByUsername(username);

        //then
        assertEquals(expected, actual);
        verify(authorRepository).findByUsername(username);
    }

    @Test
    void shouldCreateAuthor() {
        // given
        AuthorDto authorForSave = AuthorTestData.builder()
                .build()
                .buildAuthorDto();
        AuthorDto expected = AuthorTestData.builder()
                .build()
                .buildAuthorDto();
        Author author = AuthorTestData.builder()
                .build()
                .buildAuthor();

        when(authorMapper.dtoToEntity(authorForSave))
                .thenReturn(author);
        when(authorRepository.save(author))
                .thenReturn(author);
        when(authorMapper.entityToDto(author))
                .thenReturn(expected);


        //when
        AuthorDto actual = authorService.save(authorForSave);

        //then
        verify(authorRepository).save(author);

        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateAuthor() {
        // given
        AuthorDto authorDtoForUpdate = AuthorTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildAuthorDto();
        Optional<Author> optionalAuthorFromDB = Optional.of(AuthorTestData.builder()
                .build()
                .buildAuthor());
        Author author = AuthorTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildAuthor();
        AuthorDto authorFromDB = AuthorTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildAuthorDto();
        Long id = ConstantsForTest.AUTHOR_ID;

        when(authorRepository.findById(id)).thenReturn(optionalAuthorFromDB);
        when(authorRepository.save(any())).thenReturn(author);
        when(authorMapper.entityToDto(author))
                .thenReturn(authorFromDB);

        //when
        AuthorDto expected = authorService.update(authorDtoForUpdate);

        //then
        verify(authorRepository).save(any());

        assertThat(authorDtoForUpdate)
                .hasFieldOrPropertyWithValue(Author.Fields.id, expected.getId())
                .hasFieldOrPropertyWithValue(Author.Fields.username, expected.getUsername())
                .hasFieldOrPropertyWithValue(Author.Fields.address, expected.getAddress())
                .hasFieldOrPropertyWithValue(Author.Fields.email, expected.getEmail())
                .hasFieldOrPropertyWithValue(Author.Fields.telephone, expected.getTelephone())
                .hasFieldOrPropertyWithValue(Author.Fields.registrationDate, expected.getRegistrationDate());
    }

    @Test
    void shouldNotUpdateAuthorAndThrowsAuthorNotFoundException() {
        // given
        Long id = ConstantsForTest.AUTHOR_ID;
        AuthorDto authorDtoForUpdate = AuthorTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildAuthorDto();
        String errorMessage = "Author with id: " + id + " not found";

        when(authorRepository.findById(id))
                .thenThrow(new EntityNotFoundException (Author.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            authorService.update(authorDtoForUpdate);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

}