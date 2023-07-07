package ru.moore.AISUchetTehniki.repositories.spr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    List<Location> findAllByParentIdIsNullOrderByNameDesc();

    List<Location> findAllByIdInOrderByNameAsc(List<UUID> idLocation);

    Optional<Location> findByName(String name);
}
