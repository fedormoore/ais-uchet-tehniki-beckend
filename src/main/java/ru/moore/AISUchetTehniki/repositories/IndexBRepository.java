package ru.moore.AISUchetTehniki.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.moore.AISUchetTehniki.models.Entity.IndexB;

import java.util.UUID;

@Repository
public interface IndexBRepository extends JpaRepository<IndexB, UUID> {

    @Query(value = "SELECT * FROM index_b",
            nativeQuery = true)
    IndexB findIndexB();

}
