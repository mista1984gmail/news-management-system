package com.example.service.impl;

import com.example.entity.Journalist;
import com.example.entity.dto.AuthorDto;
import com.example.entity.dto.JournalistDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.JournalistMapper;
import com.example.repository.JournalistRepository;
import com.example.util.ConstantsForTest;
import com.example.util.JournalistTestData;
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
class JournalistServiceImplTest {

    @Mock
    private JournalistRepository journalistRepository;

    @Mock
    private JournalistMapper journalistMapper;

    @InjectMocks
    private JournalistServiceImpl journalistService;

    @Test
    void shouldDeleteJournalist(){
        // given
        Long id = ConstantsForTest.JOURNALIST_ID;
        Optional<Journalist> journalist = Optional.of(JournalistTestData.builder()
                .build()
                .buildJournalist());

        when(journalistRepository.findById(id))
                .thenReturn(journalist);
        doNothing()
                .when(journalistRepository)
                .delete(journalist.get());

        //when
        journalistService.deleteById(id);

        //then
        verify(journalistRepository).delete(any());
    }

    @Test
    void shouldBlockedJournalist(){
        // given
        Long id = ConstantsForTest.JOURNALIST_ID;
        Journalist journalist = JournalistTestData.builder()
                .withIsBlocked(true)
                .build()
                .buildJournalist();
        Optional<Journalist> optionalJournalistFromDB = Optional.of(JournalistTestData.builder()
                .build()
                .buildJournalist());

        when(journalistRepository.findById(id)).thenReturn(optionalJournalistFromDB);
        when(journalistRepository.save(journalist))
                .thenReturn(journalist);

        //when
        journalistService.blocked(id);

        //then
        verify(journalistRepository).save(any());
    }

    @Test
    void shouldNotDeleteJournalistAndThrowsAuthorNotFoundException() {
        // given
        Long id = ConstantsForTest.JOURNALIST_ID;
        String errorMessage = "Journalist with id: " + id + " not found";
        Optional<Journalist> journalist = Optional.of(JournalistTestData.builder()
                .withId(null)
                .build()
                .buildJournalist());

        when(journalistRepository.findById(id))
                .thenThrow(new EntityNotFoundException(Journalist.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            journalistService.deleteById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
        verify(journalistRepository, never()).delete(journalist.get());
    }

     @Test
    void shouldNotGetJournalistByIdAndThrowsAuthorNotFoundException() {
        // given
         Long id = ConstantsForTest.JOURNALIST_ID;
         String errorMessage = "Journalist with id: " + id + " not found";

         when(journalistRepository.findById(id))
                 .thenThrow(new EntityNotFoundException(Journalist.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            journalistService.findById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

     @Test
    void shouldGetJournalistById() {
        // given
         Long id = ConstantsForTest.JOURNALIST_ID;
         Optional<Journalist> journalist = Optional.of(JournalistTestData.builder()
                 .withId(null)
                 .build()
                 .buildJournalist());
        JournalistDto expected = JournalistTestData.builder()
                .build()
                .buildJournalistDto();

        when(journalistRepository.findById(id))
                .thenReturn(journalist);
        when(journalistMapper.entityToDto(journalist.get()))
                .thenReturn(expected);

        //when
        JournalistDto actual = journalistService.findById(id);

        //then
        assertEquals(expected, actual);
        verify(journalistRepository).findById(id);
        verify(journalistMapper).entityToDto(journalist.get());
    }

    @Test
    void shouldGetJournalistByUsername() {
        // given
        String username = ConstantsForTest.JOURNALIST_USERNAME;
        Optional<Journalist> journalist = Optional.of(JournalistTestData.builder()
                .build()
                .buildJournalist());
        Journalist expected = JournalistTestData.builder()
                .build()
                .buildJournalist();

        when(journalistRepository.findByUsername(username))
                .thenReturn(journalist);

        //when
        Journalist actual = journalistService.findByUsername(username);

        //then
        assertEquals(expected, actual);
        verify(journalistRepository).findByUsername(username);
    }

    @Test
    void shouldCreateJournalist() {
        // given
        JournalistDto journalistDtoForSave = JournalistTestData.builder()
                .build()
                .buildJournalistDto();
        JournalistDto expected = JournalistTestData.builder()
                .build()
                .buildJournalistDto();
        Journalist journalist = JournalistTestData.builder()
                .build()
                .buildJournalist();

        when(journalistMapper.dtoToEntity(journalistDtoForSave))
                .thenReturn(journalist);
        when(journalistRepository.save(journalist))
                .thenReturn(journalist);
        when(journalistMapper.entityToDto(journalist))
                .thenReturn(expected);


        //when
        JournalistDto actual = journalistService.save(journalistDtoForSave);

        //then
        verify(journalistRepository).save(journalist);

        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateJournalist() {
        // given
        JournalistDto journalistDtoForUpdate = JournalistTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildJournalistDto();
        Optional<Journalist> optionalJournalistFromDB = Optional.of(JournalistTestData.builder()
                .build()
                .buildJournalist());
        Journalist journalist = JournalistTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildJournalist();
        JournalistDto journalistFromDB = JournalistTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildJournalistDto();
        Long id = ConstantsForTest.JOURNALIST_ID;

        when(journalistRepository.findById(id)).thenReturn(optionalJournalistFromDB);
        when(journalistRepository.save(any())).thenReturn(journalist);
        when(journalistMapper.entityToDto(journalist))
                .thenReturn(journalistFromDB);

        //when
        JournalistDto expected = journalistService.update(journalistDtoForUpdate);

        //then
        verify(journalistRepository).save(any());

        assertThat(journalistDtoForUpdate)
                .hasFieldOrPropertyWithValue(Journalist.Fields.id, expected.getId())
                .hasFieldOrPropertyWithValue(Journalist.Fields.username, expected.getUsername())
                .hasFieldOrPropertyWithValue(Journalist.Fields.address, expected.getAddress())
                .hasFieldOrPropertyWithValue(Journalist.Fields.email, expected.getEmail())
                .hasFieldOrPropertyWithValue(Journalist.Fields.telephone, expected.getTelephone())
                .hasFieldOrPropertyWithValue(Journalist.Fields.registrationDate, expected.getRegistrationDate());
    }

    @Test
    void shouldNotUpdateAuthorAndThrowsAuthorNotFoundException() {
        // given
        Long id = ConstantsForTest.JOURNALIST_ID;
        JournalistDto journalistDtoForUpdate = JournalistTestData.builder()
                .withUsername("Daina")
                .withAddress("Apt. 187 79965 Mosciski Rapid, West Lemuel, NH 25422-4462")
                .build()
                .buildJournalistDto();
        String errorMessage = "Journalist with id: " + id + " not found";

        when(journalistRepository.findById(id))
                .thenThrow(new EntityNotFoundException (Journalist.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            journalistService.update(journalistDtoForUpdate);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

}