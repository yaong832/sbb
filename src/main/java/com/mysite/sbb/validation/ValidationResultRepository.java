package com.mysite.sbb.validation;

//Optional 사용을 위한 import
import java.util.Optional;

//JPA Repository import
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationResultRepository extends JpaRepository<ValidationResult, Integer>{
	Optional<ValidationResult> findTopByOrderByIdDesc();
}
