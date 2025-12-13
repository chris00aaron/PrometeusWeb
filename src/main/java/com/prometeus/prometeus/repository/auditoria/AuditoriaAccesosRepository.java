package com.prometeus.prometeus.repository.auditoria;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.prometeus.prometeus.model.AuditoriaAccesos;

@Repository
public interface AuditoriaAccesosRepository extends JpaRepository<AuditoriaAccesos, Long> {
    Page<AuditoriaAccesos> findByFechaCreacionUsuarioBetweenAndUsernameAdminContainingIgnoreCaseAndRolNuevoContainingIgnoreCase(
            LocalDateTime start, LocalDateTime end, String usernameAdmin, String rolNuevo, Pageable pageable);

    List<AuditoriaAccesos> findTop5ByOrderByFechaCreacionUsuarioDesc();
}
