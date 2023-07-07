package ru.moore.AISUchetTehniki.services.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import ru.moore.AISUchetTehniki.models.Dto.spr.UserDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Page<UserDto> getAllUserPage(Specification<User> spec, int page, int pageSize);

    List<UserDto> saveUserDTOList(List<UserDto> userDtoList);

    User saveUser(User user);

    ResponseEntity<?> deleteUser(List<UserDto> userDtoList);

    List<UserDto> getAllUserList();

    Optional<User> findById(UUID id);
    Optional<User> findByLastNameAndFirstNameAndMiddleNames(String lastName, String firstName, String middleNames);
}
