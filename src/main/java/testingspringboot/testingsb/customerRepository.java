package testingspringboot.testingsb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface customerRepository extends JpaRepository<customerinfo, Long> {

    public Optional<customerinfo> findByName(String name);

    public List<customerinfo> findByNameContainingIgnoreCase(String name);

    public boolean existsByEmail(String string);
}
