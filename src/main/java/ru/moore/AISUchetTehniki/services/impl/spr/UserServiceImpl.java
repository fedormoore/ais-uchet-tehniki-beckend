package ru.moore.AISUchetTehniki.services.impl.spr;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.enums.OrganizationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.spr.UserDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.models.Entity.spr.Organization;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.repositories.spr.UserRepository;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.services.spr.OrganizationService;
import ru.moore.AISUchetTehniki.services.spr.UserService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LocationService locationService;
    private final OrganizationService organizationService;
    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MapperUtils mapperUtils;

    @Override
    public Page<UserDto> getAllUserPage(Specification<User> spec, int page, int pageSize) {
        return userRepository.findAll(spec, PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "lastName"))).map(this::toDtoUser);
    }

    @Override
    public List<UserDto> getAllUserList() {
        return mapperUtils.mapAll(userRepository.findAll(), UserDto.class);
    }

    @Override
    @Transactional
    public List<UserDto> saveUserDTOList(List<UserDto> userDtoList) {
        List<User> returnUser = new ArrayList<>();
        for (UserDto userDto : userDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ USER ПО ID
            User user = findById(userDto.getId()).orElse(new User());
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Location location = locationService.findById(userDto.getLocationId()).orElse(null);
            if (location != null && !location.getType().equals(LocationTypeEnum.CABINET.getName())) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Необходимо выбрать кабинет");
            }
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
            Organization organization = organizationService.findById(userDto.getOrganizationId()).orElse(null);
            if (organization != null && !organization.getType().equals(OrganizationTypeEnum.STRUCTURE.getName())) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Необходимо выбрать кабинет");
            }

            user.setEmail(userDto.getEmail());
            user.setTelephone(userDto.getTelephone());
            user.setLastName(userDto.getLastName());
            user.setFirstName(userDto.getFirstName());
            user.setMiddleNames(userDto.getMiddleNames());
            user.setOrganizationFunction(userDto.getOrganizationFunction());
            user.setLocation(location);
            user.setOrganization(organization);

            userRepository.save(user);
            returnUser.add(user);
        }
        return mapperUtils.mapAll(returnUser, UserDto.class);
    }

    @Override
    public User saveUser(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteUser(List<UserDto> userDtoList) {
        for (UserDto userDto : userDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            User user = findById(userDto.getId()).orElse(null);

            List<MaterialValueOrg> materialValueOrg = materialValueOrgRepository.findByUserId(Objects.requireNonNull(user).getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            materialValueOrg = materialValueOrgRepository.findByResponsibleId(user.getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            user.setDeleted(true);
            userRepository.save(user);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private UserDto toDtoUser(User user) {
        return mapperUtils.map(user, UserDto.class);
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id != null) {
            Optional<User> userFind = userRepository.findById(id);
            if (userFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Сотрудник с ID " + id + " не найден!");
            }
            return userFind;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByLastNameAndFirstNameAndMiddleNames(String lastName, String firstName, String middleNames) {
        Optional<User> userFind = userRepository.findByLastNameAndFirstNameAndMiddleNames(lastName, firstName, middleNames);
        return userFind;
    }
}
