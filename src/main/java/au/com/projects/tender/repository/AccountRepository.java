package au.com.projects.tender.repository;

import au.com.projects.tender.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByEmail(String email);
    Account findByEmailHashed(String emailHashed);

    List<Account> findByEmailContaining(String email);

    Integer countByRole(String role);

}
