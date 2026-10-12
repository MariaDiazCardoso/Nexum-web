package com.nexum.repository;

import com.nexum.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** No lleva @Repository: Spring Data la detecta automáticamente. */
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, UUID id);
}

